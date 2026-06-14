#!/bin/bash
if [[ $# -eq 0 ]]; then
    jsiv/jsiv
else
    jsiv/jsiv "$1"
fi
