package Servidor;

import java.io.*;
import java.net.*;

public class Servidor 
{
	//Puerto que se encuentra a la escucha
	static final int puerto = 2000;
		
	//Socket del servidor
	ServerSocket skServidor = null;
	
	//Lector que leerá el texto de apagar en el servidor
	BufferedReader lector = null;
	
	//Carpeta que almacena los ficheros del servidor
	File carpetaFicherosServidor = null;
	
	public Servidor() throws IOException
	{
		//Iniciamos el servidor
		skServidor = new ServerSocket(puerto);		

		//Inicializamos el lector
		lector = new BufferedReader(new InputStreamReader(System.in));
		
		//Obtenemos la ruta de la carpeta
		carpetaFicherosServidor = new File("src/ArchivosServidor");
		
		//Avisamos como apagar el servidor
		System.out.println("Escriba 'apagar' para cerrar el servidor");
		
		//Mostramos un mensaje del puerto en el que se encuentra el servidor
		System.out.println("El servidor se encuentra a la escucha en el puerto 2000");
		
		//Hilo que acepta los sockets de los clientes y los gestiona
		new HiloAceptarSockets(this).start();
		
		//Si el usuario escribe apagar, apagamos el servidor
		while (!lector.readLine().equals("apagar"))
		{}
		
		System.out.print("Servidor apagandose");
		
		//Apagamos el socket del servidor
		skServidor.close();
		
	}
	
	public static void main(String[] args) throws IOException 
	{
		//Iniciamos el servidor
		new Servidor();

	}

}
