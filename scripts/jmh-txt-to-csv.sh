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

function txtToCsv() {
	local txt
	if (($# > 1)); then
		txt=$1
	else
		txt="$scriptsDir/../build/results/jmh/results.txt"
	fi
	cp "$txt" "$txt.csv"
	perl -i -0777 -pe 's/((.*:·gc\.(alloc\.rate|count|time)   .*\n)| ±|.*Bench\.)//g' "$txt.csv"
	perl -i -0777 -pe 's/ +/\t/g' "$txt.csv"

}

${__SOURCED__:+return}
txtToCsv "@"
