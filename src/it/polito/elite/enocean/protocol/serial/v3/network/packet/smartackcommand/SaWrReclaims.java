package it.polito.elite.enocean.protocol.serial.v3.network.packet.smartackcommand;

import it.polito.elite.enocean.protocol.serial.v3.network.packet.Packet;

/**
 * Set the amount of reclaim tries in Smart Ack Client
 * 
 * @author andreabiasi
 *
 */
public class SaWrReclaims extends Packet{
	/**
	 * @param reclaimCount : Presetting for the number of required reclaim tries
	 */
	public SaWrReclaims(byte reclaimCount){
		super();
		this.packetType = 0x06;
		//Smart ack code
		this.data[0] = 0x07;
		this.data[1] = reclaimCount;
		this.buildPacket();
	}
}