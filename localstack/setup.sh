#!/bin/bash

echo "> execute scripts"
for script in /opt/scripts/*.sh; do
    echo "> script: $script"
    chmod +x "$script"
    bash "$script"
done
