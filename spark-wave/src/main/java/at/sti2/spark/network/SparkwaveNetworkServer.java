/*
 * Copyright (c) 2012, University of Innsbruck, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package at.sti2.spark.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import at.sti2.spark.core.stream.Triple;

public class SparkwaveNetworkServer extends Thread{
	
	static Logger logger = Logger.getLogger(SparkwaveNetworkServer.class);

	private ExecutorService sparkwaveParserExecutor = Executors.newSingleThreadExecutor(); 
	private List<BlockingQueue<Triple>> queues = null;
	private int serverPort = 0;
	
	public SparkwaveNetworkServer(int serverPort, List<BlockingQueue<Triple>> queues){
		this.queues = queues;
		this.serverPort = serverPort;
		setName(getName()+"-Server");
	}
	
	
	/**
	 * UNIX Domain Socket Server
	 */
//	public void run(){
//		
//		//Open unix domain socket 
//		final File socketFile = new File(new File(System.getProperty("java.io.tmpdir")), "sparkweave.sock");
//		
//		AFUNIXServerSocket server;
//		
//		try {
//			server = AFUNIXServerSocket.newInstance();
//	        server.bind(new AFUNIXSocketAddress(socketFile));
//	        logger.info("Server: " + server);
//	        
//	        while (!Thread.interrupted()) {
//	            logger.info("Waiting for connection...");
//	            Socket sock = server.accept();
//	            logger.info("Connected: " + sock);
//	            (new SparkWeaveNetworkServerThread(sparkWeaveNetwork, sock)).start();
//	        }
//	        
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	/**
	 * TCP/IP Sparkweave Network Server
	 */
	public void run(){

		try {
			
			//Open TCP/IP Server socket
			ServerSocket server = new ServerSocket(serverPort);
	        logger.info("Server: " + server);
	        
	        while (!Thread.interrupted()) {
	        	logger.info("Waiting for connection...");
	            Socket sock = server.accept();
	            logger.info("Connected: " + sock);
	            
	            BufferedReader streamReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	            
	            //TODO inbetween should a plugin convert from XML ->(XSLT) RDF/XML ->(LIB) NT
	            //Inputstream -> Outputstream
	            

	            
	            SparkwaveStreamParserThread sparkStreamParserThread = new SparkwaveStreamParserThread(streamReader, queues);
	            
	            sparkwaveParserExecutor.execute(sparkStreamParserThread);
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
}
