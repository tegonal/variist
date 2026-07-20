//
//    __                          __
//   / /____ ___ ____  ___  ___ _/ /       This file is provided to you by https://github.com/tegonal/variist
//  / __/ -_) _ `/ _ \/ _ \/ _ `/ /        Copyright 2023 Tegonal Genossenschaft <info@tegonal.com>
//  \__/\__/\_, /\___/_//_/\_,_/_/         It is licensed under European Union Public License v. 1.2
//         /___/                           Please report bugs and contribute back your improvements
//
//                                         Version: v2.0.0-RC-3
//##################################
import {createGitHubMatrixBuilder} from './vlsi_matrix_builder.mjs';
import {configureKotlinDefaults, filterValues, generateJvmRows, javaVersionAxis, setMatrix} from "./matrix_commons.mjs";

const {matrix} = createGitHubMatrixBuilder();
// we no longer support jdk 11
configureKotlinDefaults(matrix, {versionAxis: filterValues(javaVersionAxis, x => x !== '11')});
const include = generateJvmRows(matrix, process.env.GITHUB_EVENT_NAME === "pull_request" ? 3 : 1)
setMatrix(matrix, include);
