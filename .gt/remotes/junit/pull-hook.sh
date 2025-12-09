#!/usr/bin/env bash
set -eu -o pipefail

function gt_pullHook_junit_before() {
	: # no op, nothing to do
}

function gt_pullHook_junit_after() {
	local -r _tag=$1 _source=$2 target=$3

	if [[ $target =~ .*/CollectionUtils.java ]]; then
		perl -0777 -i \
			-pe 's/import static org.junit.platform.commons.util.ReflectionUtils.invokeMethod;/import static org.junit.platform.commons.support.ReflectionSupport.invokeMethod;\nimport org.junit.platform.commons.support.ReflectionSupport;/g;' \
			-pe 's/ReflectionUtils/ReflectionSupport/g;' \
			"$target" || return $?
	elif [[ $target =~ .*/ReflectionUtils.java ]]; then
		local importUntilClass='(package org.junit.*\n)[\S\s]+(/\*\*[\S\s]+)\n \*/\n([\S\s]+public final class ReflectionUtils \{)'
		# shellcheck disable=SC2155
		local importUntilClassReplacement=$(
			cat <<-EOM
				\$1
				import org.apiguardian.api.API;
				import org.junit.platform.commons.support.ReflectionSupport;
				import org.junit.platform.commons.JUnitException;

				import static java.lang.String.format;
				import static org.apiguardian.api.API.Status.INTERNAL;

				\$2
				 *
				 * Modified for Variist, see pull-hook.sh
				 * We removed all function we don't use, which we can use via ReflectionSupport respectively
				 */
				\$3
			EOM
		)
		local loadRequired='(/\*\*[\S\s]+\*/\n\s+\@API.*\n\s+public static Class<\?> loadRequiredClass[\S\s]+?\n	\})'
		local parseMethodName='(/\*\*[\S\s]+\*/\n\s+public static String\[\] parseFullyQualifiedMethodName[\S\s]+?\n	\})'
		perl -0777 -i \
			-pe "s@${importUntilClass}[\S\s]+${loadRequired}[\S\s]+${parseMethodName}[\S\s]+\n\}@${importUntilClassReplacement}\$4\n\n	\$5\n}@g;" \
			-pe 's/return tryToLoadClass/return ReflectionSupport.tryToLoadClass/g;' \
			"$target" || return $?

	elif [[ $target =~ .*/MethodArgumentsProvider.java ]]; then
		local importUntilClass='(package org.junit.*\n)[\S\s]+(/\*\*[\S\s]+)\n \*/\nclass MethodArgumentsProvider extends AnnotationBasedArgumentsProvider<MethodSource> \{'
		# shellcheck disable=SC2155
		local importUntilClassReplacement=$(
			cat <<-EOM
				\$1
				import com.tegonal.variist.export.org.junit.platform.commons.util.ClassLoaderUtils;
				import com.tegonal.variist.export.org.junit.platform.commons.util.Preconditions;
				import com.tegonal.variist.export.org.junit.platform.commons.util.ReflectionUtils;
				import com.tegonal.variist.export.org.junit.platform.commons.util.StringUtils;
				import org.junit.jupiter.api.Test;
				import org.junit.jupiter.api.TestFactory;
				import org.junit.jupiter.api.TestTemplate;
				import org.junit.platform.commons.support.HierarchyTraversalMode;
				import org.junit.platform.commons.support.ReflectionSupport;

				import java.lang.reflect.Method;
				import java.lang.reflect.Modifier;
				import java.util.List;
				import java.util.Optional;
				import java.util.function.Predicate;

				import static com.tegonal.variist.export.org.junit.platform.commons.util.CollectionUtils.isConvertibleToStream;
				import static java.lang.String.format;
				import static java.util.stream.Collectors.toList;
				import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

				\$2
				 *
				 * Modified for Variist, see pull-hook.sh
				 * MethodArgumentsProvider usually extends AnnotationBasedArgumentsProvider
				 * (in org.junit.jupiter.params.provider). Variist is only interested in its static functions.
				 * and has removed provideArguments
				 */
				public class MethodArgumentsProvider {
			EOM
		)
		local provideArguments='\n\s*@Override.*\n\s*protected Stream<\? extends Arguments> provideArguments[\S\s]+?\n	\}'

		perl -0777 -i \
			-pe "s@${importUntilClass}@${importUntilClassReplacement}@g;" \
			-pe "s/${provideArguments}//g;" \
			-pe 's/private (static Method (?:findFactoryMethod|validateFactoryMethod)\()/public $1/g;' \
			-pe 's/ReflectionUtils\.findMethods\(clazz, isCandidate\)/ReflectionSupport.findMethods(clazz, isCandidate, HierarchyTraversalMode.TOP_DOWN)/g;' \
			-pe 's/ReflectionUtils\.findMethod/ReflectionSupport.findMethod/g;' \
			-pe 's/ReflectionUtils.isStatic\(factoryMethod\)/Modifier.isStatic(factoryMethod.getModifiers())/g;' \
			-pe 's/isConvertibleToStream\(method.getReturnType\(\)\) && //g;' \
			"$target" || return $?
	fi

	# shellcheck disable=SC2155
	local warning=$(
		cat <<-EOM
			// -----------------------------------------------------------------------------------------------------
			// WARNING !!!!!!!!!!!!!!
			// Copied from JUnit and exported for internal use in Variist only
			// No backward compatibility guarantees. As soon as JUnit breaks compatibility (which can happen
			// even in a patch version and that is totally fine) and we \`gt update\` this file to this new version
			// (maybe also in a patch version), we will break compatibility as well.
			// -----------------------------------------------------------------------------------------------------
		EOM
	)

	perl -0777 -i \
		-pe "s@package org.junit@${warning}\npackage com.tegonal.variist.export.org.junit@g;" \
		-pe 's/(import (?:static )?)(org.junit.platform.commons.(util|logging))/$1com.tegonal.variist.export.$2/g;' \
		-pe 's/\npublic ((?:final)? class (?:ClassNamePatternFilterUtils|ExceptionUtils|UnrecoverableExceptions))/\n$1/g;' \
		"$target" || return $?
}
