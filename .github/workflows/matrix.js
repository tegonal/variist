//
//    __                          __
//   / /____ ___ ____  ___  ___ _/ /       This file is provided to you by https://github.com/tegonal/variist
//  / __/ -_) _ `/ _ \/ _ \/ _ `/ /        Copyright 2023 Tegonal Genossenschaft <info@tegonal.com>
//  \__/\__/\_, /\___/_//_/\_,_/_/         It is licensed under European Union Public License v. 1.2
//         /___/                           Please report bugs and contribute back your improvements
//
//                                         Version: v2.0.0-RC-3
//##################################
// adapted version of https://github.com/vlsi/github-actions-random-matrix/blob/main/examples/matrix.js
//##################################
const {MatrixBuilder} = require('./matrix_builder');
const {configureKotlinDefaults, javaDistributionAxis, javaVersionAxis, setMatrix} = require('./matrix_commons');

const matrix = new MatrixBuilder();
// we are not ready yet for jdk25 as it requires gradle 9.x which no longer supports jdk11 but we still want
// to support jdk11 for now
const withoutJdk25 = {...javaVersionAxis, values: javaVersionAxis.values.filter(x => x != '25') };
configureKotlinDefaults(matrix, javaDistributionAxis, withoutJdk25);

setMatrix(matrix, 4);
