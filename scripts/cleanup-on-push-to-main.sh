#!/usr/bin/env bash
#
#    __                          __
#   / /____ ___ ____  ___  ___ _/ /       This script is provided to you by https://github.com/tegonal/variist
#  / __/ -_) _ `/ _ \/ _ \/ _ `/ /        Copyright 2023 Tegonal Genossenschaft <info@tegonal.com>
#  \__/\__/\_, /\___/_//_/\_,_/_/         It is licensed under European Union Public License 1.2
#         /___/														Please report bugs and contribute back your improvements
#
#                                         Version: v0.1.0-SNAPSHOT
###################################
set -euo pipefail
shopt -s inherit_errexit || { echo >&2 "please update to bash 5, see errors above" && exit 1; }
unset CDPATH

if ! [[ -v scriptsDir ]]; then
	scriptsDir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]:-$0}")" >/dev/null && pwd 2>/dev/null)"
	readonly scriptsDir
fi

if ! [[ -v projectDir ]]; then
	projectDir="$(realpath "$scriptsDir/../")"
	readonly projectDir
fi

if ! [[ -v dir_of_tegonal_scripts ]]; then
	dir_of_tegonal_scripts="$scriptsDir/../lib/tegonal-scripts/src"
	source "$dir_of_tegonal_scripts/setup.sh" "$dir_of_tegonal_scripts"
fi

sourceOnce "$scriptsDir/run-shfmt.sh"

function cleanupOnPushToMain() {
	customRunShfmt || die "was not able to format"
	"$projectDir/gradlew" generateAll
	"$projectDir/gradlew" :readme-examples:build
	logSuccess "code generated"
}

${__SOURCED__:+return}
cleanupOnPushToMain "$@"
