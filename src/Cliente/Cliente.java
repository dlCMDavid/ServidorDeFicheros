package Cliente;

import java.io.*;
import java.net.*;

public class Cliente 
{
	//Almacena el cliente y el puerto del servidor al que se conectar
	final String HOST = "localhost";
	final int Puerto=2000;
	
	//Socket del cliente
	Socket sCliente = null;
	
	//Flujos de salida
	OutputStream os = null;
	DataOutputStream flujoSalida = null;
		
	//Flujos de entrada
	InputStream is = null;   
	DataInputStream flujoEntrada = null;
	ObjectInputStream entradaObjeto = null;
	
	//Flujo de lectura de datos
	BufferedReader lector = null;
	
	//Almacena el booleano que controla si terminar el programa o no
	Boolean salir = false;
	
	public Cliente() 
	{
		 
		 try
		 {
			 //Creamos el socket
			 sCliente = new Socket( HOST , Puerto );
			 
			//Creamos los flujos de salida
			os = sCliente.getOutputStream();
			flujoSalida = new DataOutputStream(sCliente.getOutputStream());
			
			//Creamos los flujos de entrada
			is = sCliente.getInputStream();
			flujoEntrada = new DataInputStream(sCliente.getInputStream());
			entradaObjeto = new ObjectInputStream(sCliente.getInputStream());
			lector = new BufferedReader(new InputStreamReader(System.in));
			 
			 //--------------------------MENSAJE DE BIENVENIDA---------------------------
			 System.out.println(flujoEntrada.readUTF());
			 
			 //-------------------------- ELECCION DEL USUARIO ------------------------------
			 //Mientras salir se a falso
			 while(!salir)
			 {
				 this.EleccionUsuario();
			 }
			 
			 
			 
		 } 
		 catch( Exception e ) 
		 {
			 System.out.println( e.getMessage() );
		 }
	}
	
	
	
	//Controlamos que decide hacer el usuario
	private void EleccionUsuario() throws Exception
	{
		String opcion = null;
		try 
		{
			//Preguntamos al usuario que desea hacer
			System.out.println(flujoEntrada.readUTF());
				 
			//Enviamos lo que el cliente quiere hacer
			opcion = lector.readLine().trim();
			flujoSalida.writeUTF(opcion);
				
			//Mientras el usuario no introduzca una opci�n valida
			while(flujoEntrada.readBoolean() == false)
			{		
				System.out.println("No se ha introducido una opción valida. Introduzca otra:");
					 
				//Enviamos la respuesta del usuario
				opcion = lector.readLine().trim();
				flujoSalida.writeUTF(opcion);
			}
			
			//Comprobamos la opción del usuario
			switch (opcion) 
			{
				case "1": 
					//Recibe los ficheros que tiene el servidor y los muestra
					this.MostrarFicheros();
					
					break;
				case "2":
					//Descarga un fichero del servidor
					this.DescargarComprobarFicheroServidor();
					
					break;
				case "3":
					//Enviar archivo
					this.EnviarComprobarArchivo();
					
					break;
				case "4":
					//Cambiamos el valor de salir
					System.out.println("--------------------------------------------------------------------");
					System.out.println("Gracias por confiar en nuestro servidor");
					System.out.println("--------------------------------------------------------------------");

					salir = true;
					break;
			}
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
	}		
	
	
	
	//Método que comprueba si en el sevidor se encuentra el archivo
	private void EnviarComprobarArchivo() throws Exception 
	{
		String nombreArchivo = null;
		
		//El usuario podra introducir hasta 3 nombres
		int veces = 2;
		
		
		try 
		{
			System.out.println("--------------------------------------------------------------------");
			
			//Preguntamos al usuario que desea hacer
			System.out.println("Introduzca el nombre con la ruta del archivo que desea enviar:");
				 
			//Almacenamos el nombre del fichero
			nombreArchivo = lector.readLine().trim();
			
			//Comprobamos si el archivo existe
			while (!new File(nombreArchivo).exists()) 
			{
				//Preguntamos al usuario que desea hacer
				System.out.println("El archivo introducido no existe. Introduzca otro: ");
					 
				//Almacenamos el nombre del fichero y lo enviamos
				nombreArchivo = lector.readLine().trim();
				
			}
			
			//Enviamos el nombre
			flujoSalida.writeUTF(nombreArchivo);
			
			//Mientras el usuario introduzca un nombre no valido
			while(flujoEntrada.readBoolean() == true && veces > 0)
			{		
				//Le restamos uno a las veces
				veces--;
				
				System.out.println("Un fichero con ese nombre ya se encuentra en el servidor. Introduzca otra ruta: ");
				
				//Almacenamos el nombre del fichero y lo enviamos
				nombreArchivo = lector.readLine().trim();
				
				//Si el archivo no existe
				while (!new File(nombreArchivo).exists()) 
				{
					//Preguntamos al usuario que desea hacer
					System.out.println("El archivo introducido no existe. Introduzca otro: ");
						 
					//Almacenamos el nombre del fichero y lo enviamos
					nombreArchivo = lector.readLine().trim();
					
				}
				
				//Enviamos la respuesta del usuario
				flujoSalida.writeUTF(nombreArchivo);
			}
			
			//Comrobamos is se ha salido del bucle porque se complio la condición o poruqe se acabaron las veces
			if (veces == 0) 
			{
				System.out.println("No ha introducido ninguna ruta valido...");
			}
			else 
			{
				//Enviamos el fichero al servidor
				this.EnviarFichero(nombreArchivo);
			}
			
			
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
	}

	//Envia el archivo
	private void EnviarFichero(String nombreArchivo) throws Exception 
	{
		
				//Almacena el fichero
				File ficheroUsuario;
				
				//Almacenará la cadena de bytes
				byte[] arrFichero = null;
				
				//Flujos para almacenar el fichero
		        BufferedInputStream bis = null;
		 
		        
				try 
				{
					//Obtenemos el fichero
					ficheroUsuario = new File(nombreArchivo);
					
					//Enviamos el tamaño del fichero
					flujoSalida.writeLong(ficheroUsuario.length());
					
					//Creamos el array del buffered que se enviará
					arrFichero = new byte[(int) ficheroUsuario.length()];
					
					//Añadimos a los flujos que leen el fichero los datos
					bis = new BufferedInputStream(new FileInputStream(ficheroUsuario));
					
					//Leemos los datos en el buffer
					bis.read(arrFichero,0,arrFichero.length);
					
					//Enviamos el fichero
					flujoSalida.write(arrFichero,0,arrFichero.length);
					
					//Limpiamos el flujo
					flujoSalida.flush();
					
					System.out.println("El fichero se ha enviado con exito");
					System.out.println("--------------------------------------------------------------------");
				} 
				catch (Exception e) 
				{
					throw new Exception(e.getMessage());
				}
				finally 
				{
					//Cerramos los flujos de ficheros
					bis.close();
				}
	}


	//Permite obtener un fichero del servidor y lo almacena en la ruta seleccioanda por el usuario
	private void DescargarComprobarFicheroServidor() throws Exception 
	{
		String nombreArchivo = null;
		
		//El usuario podra introducir hasta 3 nombres
		int veces = 2;
		
		
		try 
		{
			System.out.println("--------------------------------------------------------------------");
			
			//Preguntamos al usuario que desea hacer
			System.out.println("Introduzca el nombre del fichero que desea descargar:");
				 
			//Almacenamos el nombre del fichero y lo enviamos
			nombreArchivo = lector.readLine().trim();
			flujoSalida.writeUTF(nombreArchivo);
			
			//Mientras el usuario introduzca un nombre no valido
			while(flujoEntrada.readBoolean() == false && veces > 0)
			{		
				//Le restamos uno a las veces
				veces--;
				
				System.out.println("No se ha introducido una opción valida. Introduzca otra:");
					 
				//Enviamos la respuesta del usuario
				nombreArchivo = lector.readLine().trim();
				flujoSalida.writeUTF(nombreArchivo);
			}
			
			//Comrobamos is se ha salido del bucle porque se complio la condición o poruqe se acabaron las veces
			if (veces == 0) 
			{
				System.out.println("No ha introducido ningun nombre valido...");
			}
			else 
			{
				this.DescargarFichero(nombreArchivo);
			}
			
			
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
	}
	
	//Descarga el fichero del Servidor
	private void DescargarFichero(String nombreArchivo) throws Exception
	{				
		//Almacenará la cadena de bytes
		byte[] arrFichero = null;
				
		//Flujos para almacenar el fichero
		BufferedOutputStream bos = null;
		    
		//Valor que se lee
		int valor = 0;
		
		//Almacena la ruta donde se guarda el fichero
		String ruta = null;
		
		try 
		{
			//Preguntamos al usuario por la ruta
			System.out.println("Introduzca la ruta donde quiere guardar el fichero: ");
			ruta = lector.readLine();
			
			//Creamos el buffer con el tamaño del array
			arrFichero = new byte[(int) flujoEntrada.readLong()];
			
			//Obtenemos el fichero recibido
			bos = new BufferedOutputStream(new FileOutputStream(ruta + "/" + nombreArchivo));
			
			//Leemos los datos
            while(valor < arrFichero.length)
            {
                arrFichero[valor] = flujoEntrada.readByte();
                valor++;
            }
			
			//Grabamos los byte en el fichero
			bos.write(arrFichero);
			
			System.out.println("El fichero se ha recibido con exito");
			System.out.println("--------------------------------------------------------------------");
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		finally 
		{
			//Cerramos los flujos de ficheros
			bos.close();
		}
	}



	//Método que recibe una lista con los ficheros que se encuentran en el servidor
	private void MostrarFicheros() throws Exception 
	{
		//Almacenará el nombre de los ficheros de la carpeta
		String[] ficherosCarpeta = null;
				
		try 
		{
			
			System.out.println("--------------------------------------------------------------------");
			//Recibimos la lista de ficheros
			ficherosCarpeta = (String[]) entradaObjeto.readObject();
			
			//Comprobamos si la lista esta vacia
			if (ficherosCarpeta.length == 0) 
			{
				System.out.println("El servidor no contiene ningun fichero");
			}
			else 
			{
				System.out.println("El servidor contiene esto ficheros: ");
				
				//Recorremos la lista de ficheros y mostramos los nombres
				for (String nombreFichero : ficherosCarpeta) 
				{
					System.out.println(nombreFichero);
				}
				
			}
			
			System.out.println("--------------------------------------------------------------------");
			
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
	}


	//Ejecuta el cliente
	public static void main(String[] args) 
	{
		//Creamos un nuevo cliente
		new Cliente();
	}

}
