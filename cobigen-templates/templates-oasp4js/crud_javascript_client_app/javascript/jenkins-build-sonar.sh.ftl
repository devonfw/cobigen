#!/bin/bash
npm install
gulp build:ci
gulp sonar --login $1 --password $2
