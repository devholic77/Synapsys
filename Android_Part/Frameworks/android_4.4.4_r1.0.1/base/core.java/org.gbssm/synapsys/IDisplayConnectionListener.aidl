package org.gbssm.synapsys;

/** @hide */
interface IDisplayConnectionListener {
    
    oneway void onConnected(Socket displaySock);
    
    oneway void onDisconnected();
}
