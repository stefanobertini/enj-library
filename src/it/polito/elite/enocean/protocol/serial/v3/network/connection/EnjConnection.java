/**
 * 
 */
package it.polito.elite.enocean.protocol.serial.v3.network.connection;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import it.polito.elite.enocean.enj.EEP2_5.receiveEvent.PacketEventSender;
import it.polito.elite.enocean.protocol.serial.v3.network.link.PacketQueueItem;
import it.polito.elite.enocean.protocol.serial.v3.network.link.PacketReceiver;
import it.polito.elite.enocean.protocol.serial.v3.network.link.SerialPortFactory;
import it.polito.elite.enocean.protocol.serial.v3.network.link.PacketTransmitter;
import it.polito.elite.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import gnu.io.SerialPort;

/**
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 *
 */
public class EnjConnection {

	SerialPort serialPort;

	//Code ad alta priorita
	ConcurrentLinkedQueue<ESP3Packet> highPriorityTxQueue = new ConcurrentLinkedQueue<ESP3Packet>();
	ConcurrentLinkedQueue<ESP3Packet> highPriorityRxQueue = new ConcurrentLinkedQueue<ESP3Packet>();

	//Code a bassa priorita
	ConcurrentLinkedQueue<PacketQueueItem> lowPriorityTxQueue = new ConcurrentLinkedQueue<PacketQueueItem>();
	ConcurrentLinkedQueue<PacketQueueItem> lowPriorityRxQueue = new ConcurrentLinkedQueue<PacketQueueItem>();

	Semaphore expectedResponse = new Semaphore(1);

	PacketTransmitter threadWrite;


	public PacketEventSender packetSender = new PacketEventSender(this.lowPriorityRxQueue);

	/**
	 * 
	 */
	public EnjConnection(ConcurrentLinkedQueue<PacketQueueItem> lowPriorityRxQueue) {
		super();
		this.lowPriorityRxQueue = lowPriorityRxQueue;
	}

	public void startConnection() throws Exception{

		serialPort = (new SerialPortFactory()).getPort("/dev/ttyAMA0", 1000);

		//(new HighPriorityThread( highPriorityTxQueue, highPriorityRxQueue, expectedResponse)).run();

		PacketReceiver serialListener = new PacketReceiver(serialPort, highPriorityRxQueue, lowPriorityRxQueue, expectedResponse);
		serialPort.addEventListener(serialListener);
		serialPort.notifyOnDataAvailable(true);
		
		//PacketEventSender packetSender = new PacketEventSender(this.lowPriorityRxQueue);
		packetSender.start();

		threadWrite = new PacketTransmitter(highPriorityTxQueue, lowPriorityTxQueue, serialPort, expectedResponse);
		threadWrite.start();
		System.out.println("");
	}
	
	

	public void send(ESP3Packet pkt){
		//System.out.println("Sono in send packet");
		lowPriorityTxQueue.add( new PacketQueueItem(pkt,3));
	}


	public ESP3Packet receive(){
//		return this.lowPriorityRxQueue.peek().getPkt();
		int size = lowPriorityRxQueue.size();
		System.out.println("Elementi in coda: "+size);
		if(size!=0){
		ESP3Packet pkt = this.lowPriorityRxQueue.poll().getPkt();
		return pkt;
		}
		//return pkt;
		return null;
	}
}