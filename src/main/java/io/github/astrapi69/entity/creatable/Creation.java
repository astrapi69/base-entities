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
package io.github.astrapi69.entity.creatable;

import java.io.Serializable;

import io.github.astrapi69.data.creatable.IdentifiableCreatable;
import io.github.astrapi69.entity.identifiable.SequenceBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * The entity class {@link Creation} is keeping the information for the creation of an entity. This
 * entity can be extended or attached to another entity for keep information when it was created.
 *
 * @param <PK>
 *            the generic type of the id
 * @param <T>
 *            the generic type of time measurement
 */
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class Creation<PK extends Serializable, T> extends SequenceBaseEntity<PK>
	implements
		IdentifiableCreatable<PK, T>
{

	/** The date and time when the entity that owns this entity was created. */
	private T created;

}
