#!/bin/bash
set -e

if [ -z "$MAPBOX_ACCESS_TOKEN" ]; then
    echo 'no MAPBOX_ACCESS_TOKEN environment set'
    exit 1
fi

function create-tile {
    tilesets upload-source mark-asuncion "$1" "$2" && \
        tilesets create mark-asuncion."$1" --recipe "$3" --name "$1" && \
        tilesets publish mark-asuncion."$1"
}

if [ "$1" == "get" ]; then
    curl -O "https://api.mapbox.com/v4/mapbox.mapbox-streets-v8/16/54793/30080.mvt?style=mapbox://styles/mapbox/streets-v12@00&access_token=$MAPBOX_ACCESS_TOKEN"
elif [ "$1" == "delete" ]; then
    if [ -n "$2" ]; then
        curl -X DELETE "https://api.mapbox.com/tilesets/v1/sources/mark-asuncion/${2}?access_token=${MAPBOX_ACCESS_TOKEN}"
        echo "$(date):: deleting: ${2}" | tee -a arrow.log
    fi
elif [ "$1" == "list" ]; then
    tilesets list mark-asuncion
elif [ "$1" == "upload" ]; then
    if [[ -n $2 && -n $3 ]]; then
        tilesets upload-source mark-asuncion "$2" "$3"
        echo "$(date):: upload with name: ${2}" | tee -a arrow.log
    fi
elif [ "$1" == "create" ]; then
    if [[ -n $2 && -n $3 ]]; then
        tilesets create mark-asuncion."$2" --recipe "$3" --name "$2"
        echo "$(date):: creating recipe with name: ${2} from ${3}" | tee -a arrow.log
    fi
elif [ "$1" == "publish" ]; then
    if [[ -n $2 ]]; then
        tilesets publish mark-asuncion."$2"
        echo "$(date):: publishing with name: ${2}" | tee -a arrow.log
    fi
elif [[ $1 == "replace" ]]; then
    if [[ -n $2 && -n $3 ]]; then
        curl -X PUT "https://api.mapbox.com/tilesets/v1/sources/mark-asuncion/${2}?access_token=${MAPBOX_ACCESS_TOKEN}" \
            -F file=@${3} \
            --header "Content-Type: multipart/form-data"
        echo ''
        echo "$(date):: replacing source on: ${2} with ${3}" | tee -a arrow.log
    fi
elif [[ $1 == "--create-tile" || $1 == "-c" ]]; then
    if [[ -n $2 && -n $3 && -n $4 ]]; then
        create-tile "$2" "$3" "$4"
    fi
fi