package com.misw.medisupply.data.remote.websocket

/**
 * WebSocket connection states
 * Used to track the connection status with the backend
 */
enum class WebSocketState {
    /**
     * Attempting to connect to the WebSocket server
     */
    CONNECTING,
    
    /**
     * Successfully connected to the WebSocket server
     */
    CONNECTED,
    
    /**
     * Disconnected from the WebSocket server
     */
    DISCONNECTED,
    
    /**
     * Error occurred during connection or communication
     */
    ERROR,
    
    /**
     * Attempting to reconnect after disconnection
     */
    RECONNECTING
}
