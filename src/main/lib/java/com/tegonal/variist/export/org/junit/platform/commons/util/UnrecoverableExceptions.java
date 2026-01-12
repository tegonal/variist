/*
 * Copyright 2015-2026 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

// -----------------------------------------------------------------------------------------------------
// WARNING !!!!!!!!!!!!!!
// Copied from JUnit and exported for internal use in Variist only
// No backward compatibility guarantees. As soon as JUnit breaks compatibility (which can happen
// even in a patch version and that is totally fine) and we `gt update` this file to this new version
// (maybe also in a patch version), we will break compatibility as well.
// -----------------------------------------------------------------------------------------------------
package com.tegonal.variist.export.org.junit.platform.commons.util;

import static org.apiguardian.api.API.Status.INTERNAL;

import org.apiguardian.api.API;

/**
 * Internal utilities for working with <em>unrecoverable</em> exceptions.
 *
 * <p><em>Unrecoverable</em> exceptions are those that should always terminate
 * test plan execution immediately.
 *
 * <h2>Currently Unrecoverable Exceptions</h2>
 * <ul>
 * <li>{@link OutOfMemoryError}</li>
 * </ul>
 *
 * <h2>DISCLAIMER</h2>
 *
 * <p>These utilities are intended solely for usage within the JUnit framework
 * itself. <strong>Any usage by external parties is not supported.</strong>
 * Use at your own risk!
 *
 * @since 1.7
 */
@API(status = INTERNAL, since = "1.7")
final class UnrecoverableExceptions {

	private UnrecoverableExceptions() {
		/* no-op */
	}

	/**
	 * Rethrow the supplied {@link Throwable exception} if it is
	 * <em>unrecoverable</em>.
	 *
	 * <p>If the supplied {@code exception} is not <em>unrecoverable</em>, this
	 * method does nothing.
	 */
	public static void rethrowIfUnrecoverable(Throwable exception) {
		if (exception instanceof OutOfMemoryError) {
			throw ExceptionUtils.throwAsUncheckedException(exception);
		}
	}

}
