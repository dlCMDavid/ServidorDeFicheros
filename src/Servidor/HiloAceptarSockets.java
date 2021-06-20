package Servidor;

import java.net.*;

public class HiloAceptarSockets extends Thread
{
	Servidor server;
	
	public HiloAceptarSockets(Servidor server)
	{
		this.server = server;
	}
	
	public void run()
	{
		try 
		{
			while (true) 
			{
				//El servidor escuchar� aqu� a un potencial cliente
				Socket sCliente = server.skServidor.accept();
				
				//Escribe por pantalla a que cliente est� atendiendo
				System.out.println("Se ha recibido un cliente");
			
				//Cuando termine la escucha, lanzar� un hilo paraatenderlo
				new Hilo(sCliente,server).start();
			}
			
		} 
		catch (Exception e) 
		{
			
		}
	}
}
