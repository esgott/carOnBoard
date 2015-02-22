#!/bin/sh

set -e

for file in *_grammar.txt ; do
    base=$(basename "${file}" .txt)
    unix2dos -n "${file}" "${base}.dos.txt"
done

