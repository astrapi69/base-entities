/**
 * The MIT License
 *
 * Copyright (C) 2015 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.entity.id.generator;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.prefs.Preferences;

/**
 * Example of stackoverflow.com see
 * 'https://stackoverflow.com/questions/22416826/sequence-generator-in-java-for-unique-id'
 */
public final class SequenceGenerator
{

	private static final String DEFAULT_INITIAL_VALUE = "1";
	private static final Preferences PREFERENCES = Preferences
		.userNodeForPackage(SequenceGenerator.class);
	private static final AtomicLong ATOMIC_ID_COUNTER = new AtomicLong(
		Integer.parseInt(PREFERENCES.get("seq_id", DEFAULT_INITIAL_VALUE)));
	private static final Map<Long, SoftReference<SequenceGenerator>> GENERATORS = new ConcurrentHashMap<>();
	private static final SequenceGenerator DEFAULT_GENERATOR = new SequenceGenerator(0L,
		Long.parseLong(PREFERENCES.get("seq_0", DEFAULT_INITIAL_VALUE)));

	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			GENERATORS.values().stream().map(SoftReference::get)
				.filter(seq -> seq != null && seq.isPersistOnExit())
				.forEach(SequenceGenerator::persist);
			if (DEFAULT_GENERATOR.isPersistOnExit())
			{
				DEFAULT_GENERATOR.persist();
			}
			PREFERENCES.put("seq_id", ATOMIC_ID_COUNTER.toString());
		}));
	}

	private final long sequenceId;
	private final AtomicLong atomicIdCounter;
	private final AtomicBoolean persistOnExit = new AtomicBoolean();

	private SequenceGenerator(long sequenceId, long initialValue)
	{
		this.sequenceId = sequenceId;
		atomicIdCounter = new AtomicLong(initialValue);
	}

	public long nextId()
	{
		return atomicIdCounter.getAndIncrement();
	}

	public long currentId()
	{
		return atomicIdCounter.get();
	}

	public long getSequenceId()
	{
		return sequenceId;
	}

	public boolean isPersistOnExit()
	{
		return persistOnExit.get();
	}

	public void setPersistOnExit(boolean persistOnExit)
	{
		this.persistOnExit.set(persistOnExit);
	}

	public void persist()
	{
		PREFERENCES.put("seq_" + sequenceId, atomicIdCounter.toString());
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		GENERATORS.remove(sequenceId);
		if (persistOnExit.get())
		{
			persist();
		}
	}

	@Override
	public int hashCode()
	{
		return Long.hashCode(sequenceId);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj == this || obj != null && obj instanceof SequenceGenerator
			&& sequenceId == ((SequenceGenerator)obj).sequenceId;
	}

	@Override
	public String toString()
	{
		return "{" + "counter=" + atomicIdCounter + ", seq=" + sequenceId + '}';
	}

	public static SequenceGenerator getDefault()
	{
		return DEFAULT_GENERATOR;
	}

	public static SequenceGenerator get(long sequenceId)
	{
		if (sequenceId < 0)
		{
			throw new IllegalArgumentException("(sequenceId = " + sequenceId + ") < 0");
		}
		if (sequenceId == 0)
		{
			return DEFAULT_GENERATOR;
		}
		SoftReference<SequenceGenerator> r = GENERATORS.computeIfAbsent(sequenceId, sid -> {
			try
			{
				return new SoftReference<>(new SequenceGenerator(sid,
					Long.parseLong(PREFERENCES.get("seq_" + sid, null))));
			}
			catch (Throwable t)
			{
				return null;
			}
		});
		return r == null ? null : r.get();
	}

	public static SequenceGenerator create()
	{
		return create(1);
	}

	public static SequenceGenerator create(long initialValue)
	{
		long sequenceId = ATOMIC_ID_COUNTER.getAndIncrement();
		SequenceGenerator seq = new SequenceGenerator(sequenceId,
			Long.parseLong(PREFERENCES.get("seq_" + sequenceId, "" + initialValue)));
		GENERATORS.put(sequenceId, new SoftReference<>(seq));
		return seq;
	}

}
