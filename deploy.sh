#!/bin/bash

read -p "⚡️ Enter type: " TYPE

if [ "$TYPE" ]; then
    if [ "$TYPE" = "FIX" ]; then
        read -p "⚡️ Enter text: " TEXT
        git add --all
        git commit -m "🛠️ Fix ($TEXT)"
        git push -u origin main
        echo "⚡️ Task complete"
    fi

    if [ "$TYPE" = "UPDATE" ]; then
        read -p "⚡️ Enter text: " TEXT
        git add --all
        git commit -m "📦️ Update ($TEXT)"
        git push -u origin main
        echo "⚡️ Task complete"
    fi

    if [ "$TYPE" = "PROD" ]; then
        read -p "⚡️ Enter text: " TEXT
        git add --all
        git commit -m "🌐 Production"
        git push -u origin main
        echo "⚡️ Task complete"
    fi

    if [ "$TYPE" = "INIT" ]; then
        read -p "⚡️ Enter repositorie name: " NAME
        git init
        git add --all
        git commit -m "🚀 Init"
        git branch -M main
        git remote remove origin
        git remote add origin "$NAME"
        git push -u origin main
        echo "⚡️ Task complete"
    fi

    if [ "$TYPE" = "DEV" ]; then
        read -p "⚡️ Enter text: " TEXT
        git add --all
        git commit -m "👨‍💻 Dev ($TEXT)"
        git push -u origin main
        echo "⚡️ Task complete"
    fi
else
    echo "⚡️ Please enter deploy type"
    ./deploy.sh
fi

