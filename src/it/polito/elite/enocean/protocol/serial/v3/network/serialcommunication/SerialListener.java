/**
 * 
 */
package it.polito.elite.enocean.protocol.serial.v3.network.serialcommunication;

import it.polito.elite.enocean.protocol.serial.v3.network.packet.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 *
 */
public class SerialListener implements SerialPortEventListener{
	public SerialListener(SerialPort serialPort,
			ConcurrentLinkedQueue<Packet> highPriorityQueue,
			ConcurrentLinkedQueue<ElementQueue> lowPriorityQueue,
			Semaphore expectedResponse) {
		super();
		this.serialPort = serialPort;
		this.highPriorityRxQueue = highPriorityQueue;
		this.lowPriorityRxQueue = lowPriorityQueue;
		this.expectedResponse = expectedResponse;
	}

	// Porta seriale
	SerialPort serialPort;

	

	// flusso di dati in ingresso alla porta seriale
	InputStream in;	

	// Buffer di byte per immagazzinare i byte in arrivo dalla seriale
	byte[] receivedBytes; //Attenzione ho inizializzato per poter andare avanti nel debug
	ArrayList<Byte> buffer = new ArrayList<Byte>();

	// Coda di messaggi ad alta priorita
	ConcurrentLinkedQueue<Packet> highPriorityRxQueue;

	// Coda di messaggi a bassa priorita
	ConcurrentLinkedQueue<ElementQueue> lowPriorityRxQueue;

	// Flag per risposta attesa
	Semaphore expectedResponse;

	public void serialEvent(SerialPortEvent event) {
		try {
			in = serialPort.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Se il tipo di evento e: dati disponibili sulla seriale
		if(event.getEventType()==SerialPortEvent.DATA_AVAILABLE){

			// Flag di stato per segnalre fine della lettura dalla seriale
			int readedIntValue = 0;
			//int i=0;

			// Pacchetto ricevuto
			Packet pkt = new Packet();
			
			// Inizializzo per sicurezza: svuoto il buffer
			if(buffer.size()>0){
				buffer.clear();
			}
			System.out.println("");
			//Finche ho qualocosa leggo
			try {
				while( (in.available())>0 ){
					// Leggo i dati dall'inputstream della seriale
					readedIntValue = in.read();

					// Converto i dati interi in Byte
					byte readedByteValue = (byte) (readedIntValue & 0xff);

					if (readedByteValue==0x55){

						while(buffer.size()!=0){
							receivedBytes = new byte[this.buffer.size()];
							// Riempio il buffer dei byte arrivati
							for(int i1=0 ; i1<this.buffer.size() ; i1++){
								this.receivedBytes[i1] = this.buffer.get(i1).byteValue();
							}
							// Inpacchetta il vettore di byte in un oggetto pkt di tipo Packet
							pkt.parsePacket(this.receivedBytes); // Secondo me il packet va creato ogni volta
							putInQueue(pkt);
							buffer.clear();
						}// Fine while(buffer.size()!=0)
						
						System.out.println("" + String.format("%x", readedByteValue));

						// Aggiungo all'arraylist
						this.buffer.add(Byte.valueOf(readedByteValue));

					}
					else{
						// Aggiungo all'arraylist
						System.out.println("" + String.format("%x", readedByteValue));
						this.buffer.add(Byte.valueOf(readedByteValue));	
					}
				} // Fine while (in.available())>0
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receivedBytes = new byte[this.buffer.size()];
			// Riempio il buffer dei byte arrivati
			for(int i1=0 ; i1<this.buffer.size() ; i1++){
				this.receivedBytes[i1] = this.buffer.get(i1).byteValue();
			}
			// Inpacchetta il vettore di byte in un oggetto pkt di tipo Packet
			pkt.parsePacket(this.receivedBytes); // Secondo me il packet va creato ogni volta
			putInQueue(pkt);
			buffer.clear();
			System.out.println("Ho letto i dati");	
		}
	}

	private void putInQueue(Packet pkt){
		//Se il pacchetto ricevuto e una risposta
		if(pkt.isResponse()){
			System.out.println("Il pacchetto � una risposta");

			// Libero il flag risposta attesa
			this.expectedResponse.release();

			//Aggiungo il paccketto alla coda dati a bassa priorit�
			this.lowPriorityRxQueue.add(new ElementQueue(pkt, 3));
		}
		else{
			if(pkt.requireResponse()){
				this.highPriorityRxQueue.add(pkt);
			}
			else{
				//Aggiungo il paccketto alla coda dati a bassa priorit�
				this.lowPriorityRxQueue.add(new ElementQueue(pkt, 3));
			}
		} // Fine isResponse
	}

}