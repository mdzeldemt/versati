package com.liuvil.versati.framework.preferences.entry.content.css

// TODO: Extract default value into asset file and make configurable
const val DEFAULT_ENTRY_CONTENT_STYLESHEET = """
    a {
        color: var(--on-surface-color) !important;
    }
    
    body {
        margin: 0;
        padding 0;
        font-size: 12pt !important;
        color: var(--on-surface-color) !important;
    }
    
    h1 {
        font-size: 16pt !important;
    }
    
    h2 {
        font-size: 14pt !important;
    }
    
    h3 {
        font-size: 12pt !important;
    }

    img {
        max-width: 100%;
    }
"""