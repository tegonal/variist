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

import org.apiguardian.api.API;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.JUnitException;

import static java.lang.String.format;
import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Collection of utilities for working with the Java reflection APIs.
 *
 * <h2>DISCLAIMER</h2>
 *
 * <p>These utilities are intended solely for usage within the JUnit framework
 * itself. <strong>Any usage by external parties is not supported.</strong>
 * Use at your own risk!
 *
 * <p>Some utilities are published via the maintained {@code ReflectionSupport}
 * class.
 *
 * @since 1.0
 * @see org.junit.platform.commons.support.ReflectionSupport
 *
 * Modified for Variist, see pull-hook.sh
 * We removed all function we don't use, which we can use via ReflectionSupport respectively
 */
@API(status = INTERNAL, since = "1.0")
public final class ReflectionUtils {/**
	 * Load a class by its <em>primitive name</em> or <em>fully qualified name</em>,
	 * using the supplied {@link ClassLoader}.
	 *
	 * <p>See {@link org.junit.platform.commons.support.ReflectionSupport#tryToLoadClass(String)}
	 * for details on support for class names for arrays.
	 *
	 * @param name the name of the class to load; never {@code null} or blank
	 * @param classLoader the {@code ClassLoader} to use; never {@code null}
	 * @throws JUnitException if the class could not be loaded
	 * @since 1.11
	 * @see #tryToLoadClass(String, ClassLoader)
	 */
	@API(status = INTERNAL, since = "1.11")
	public static Class<?> loadRequiredClass(String name, ClassLoader classLoader) throws JUnitException {
		return ReflectionSupport.tryToLoadClass(name, classLoader).getOrThrow(
			cause -> new JUnitException(format("Could not load class [%s]", name), cause));
	}

	/**
	 * Parse the supplied <em>fully qualified method name</em> into a 3-element
	 * {@code String[]} with the following content.
	 *
	 * <ul>
	 *   <li>index {@code 0}: the fully qualified class name</li>
	 *   <li>index {@code 1}: the name of the method</li>
	 *   <li>index {@code 2}: a comma-separated list of parameter types, or a
	 *       blank string if the method does not declare any formal parameters</li>
	 * </ul>
	 *
	 * @param fullyQualifiedMethodName a <em>fully qualified method name</em>,
	 * never {@code null} or blank
	 * @return a 3-element array of strings containing the parsed values
	 */
	public static String[] parseFullyQualifiedMethodName(String fullyQualifiedMethodName) {
		Preconditions.notBlank(fullyQualifiedMethodName, "fullyQualifiedMethodName must not be null or blank");

		int indexOfFirstHashtag = fullyQualifiedMethodName.indexOf('#');
		boolean validSyntax = (indexOfFirstHashtag > 0)
				&& (indexOfFirstHashtag < fullyQualifiedMethodName.length() - 1);

		Preconditions.condition(validSyntax,
			() -> "[" + fullyQualifiedMethodName + "] is not a valid fully qualified method name: "
					+ "it must start with a fully qualified class name followed by a '#' "
					+ "and then the method name, optionally followed by a parameter list enclosed in parentheses.");

		String className = fullyQualifiedMethodName.substring(0, indexOfFirstHashtag);
		String methodPart = fullyQualifiedMethodName.substring(indexOfFirstHashtag + 1);
		String methodName = methodPart;
		String methodParameters = "";

		if (methodPart.endsWith("()")) {
			methodName = methodPart.substring(0, methodPart.length() - 2);
		}
		else if (methodPart.endsWith(")")) {
			int indexOfLastOpeningParenthesis = methodPart.lastIndexOf('(');
			if ((indexOfLastOpeningParenthesis > 0) && (indexOfLastOpeningParenthesis < methodPart.length() - 1)) {
				methodName = methodPart.substring(0, indexOfLastOpeningParenthesis);
				methodParameters = methodPart.substring(indexOfLastOpeningParenthesis + 1, methodPart.length() - 1);
			}
		}
		return new String[] { className, methodName, methodParameters };
	}
}
