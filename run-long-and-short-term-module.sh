#!/bin/bash
./gradlew clean --refresh-dependencies dependencies build
./gradlew runRastatt_LongTermModule
./gradlew runRastatt_100p_ShortTermModule