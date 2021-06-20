package Servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Hilo extends Thread
{

	//Referencia al servidor
	Servidor server;
	
	//Socket del cliente
	Socket sCliente;
	
	//Flujos de salida
	OutputStream os = null;
	DataOutputStream flujoSalida = null;
	ObjectOutputStream salidaObjeto = null;
	
	//Flujos de entrada
	InputStream is = null; 
	DataInputStream flujoEntrada = null;
	
	//Almacena el booleano que controla si terminar el programa o no
	Boolean salir = false;
	
	public Hilo(Socket sCliente,Servidor server)
	{
		this.sCliente = sCliente;
		this.server = server;
	}
	
	public void run()
	{
		try 
		{
			//Creamos los flujos de salida
			os = sCliente.getOutputStream();
			flujoSalida = new DataOutputStream(sCliente.getOutputStream());
			salidaObjeto = new ObjectOutputStream(sCliente.getOutputStream());
			
			//Creamos los flujos de entrada
			is = sCliente.getInputStream();
			flujoEntrada = new DataInputStream(sCliente.getInputStream());
			
			//Enviamos el mensaje inicial al usuario
			flujoSalida.writeUTF("-------------------- Servidor de Ficheros --------------------");
			
			//Mientras salir se a falso
			 while(!salir)
			 {
				 //Preguntaremos al usuario que desa hacer
				 this.OpcionesUsuario();
			 }
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void OpcionesUsuario() throws Exception 
	{
		String opcion = null;
		Boolean valido = false;
		
		try 
		{
			//Preguntamo al usuario si quiere crear un sala o unirse a una
			flujoSalida.writeUTF("Seleccione el numero: \n 1 -- Listar archivos del servidor \n 2 -- Descargar fichero del servidor"
					+ "\n 3 -- Subir archivo al servidor \n 4 -- Salir ");
			
			//Almacenamos la respuesta del usuario
			opcion = flujoEntrada.readUTF().trim();
			
			//Mientras el usuario no elija una opción correcta
			while (valido == false)
			{
				
				switch (opcion) 
				{
					case "1": 
						//Avisamos que la opcion es valida
						flujoSalida.writeBoolean(true);
						
						//Llamamos al método que enviara los datos
						this.MostrarArchivos();
						
						//Cambiamos el valor de la variables
						valido = true;
						
						break;
					case "2":
						//Avisamos que la opcion es valida
						flujoSalida.writeBoolean(true);
						
						//Envia un fichero al cliente
						this.EnviarComprobarFichero();
						
						//Cambiamos el valor de la variables
						valido = true;
						
						break;
					case "3":
						//Avisamos que la opcion es valida
						flujoSalida.writeBoolean(true);
						
						//Recibe y comprueba si el nombre
						this.RecibirComprobarFichero();
						
						//Cambiamos el valor de la variables
						valido = true;
						
						break;
					case "4":
						//Avisamos que la opcion es valida
						flujoSalida.writeBoolean(true);
						
						//Cambiamos el valor de salir
						salir = true;
						
						//Cambiamos el valor de la variables
						valido = true;
						
						break;
					default:
						//Si no se cumple ninguna, enviamos un false
						flujoSalida.writeBoolean(false);
				
						//Almacenamos la respuesta del usuario
						opcion = flujoEntrada.readUTF().trim();
						
						break;
				}
			}
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
	}

	//Método que comprueba si el archivo se encuentra en el servidor y recibe
	private void RecibirComprobarFichero() throws Exception 
	{
		//Almacenará el nombre de los ficheros de la carpeta
				String[] ficherosCarpeta = null;
				
				//Almacena las partes de la ruta del fichero para conocer su nombre
				String[] partesRuta = null;
				
				//Almacena un booleano que indica que el nombre no es valido
				Boolean nombreValido = true;
				
				//Almacena el nombre del fichero que desea el usuario
				String ficheroUsuario = null;
				
				//El usuario podra introducir hasta 3 nombres
				int veces = 3;
				
				try 
				{
					//Obtenemos la lista de nombres de ficheros
					ficherosCarpeta = server.carpetaFicherosServidor.list();
					
					//Obtenemos el nombre del fichero y comprobamos si se encuentra en el array
					while (nombreValido && veces > 0) 
					{
						//Almacenamos el fichero del usuairo
						ficheroUsuario = flujoEntrada.readUTF().trim();
						
						//Separamos el nombre de archivo en parte y comprobamos la ultima, que es la correspondiente al nomrbe
						partesRuta = ficheroUsuario.split("\\\\");
						
						//Almacenamos el nombre del fichero
						ficheroUsuario = partesRuta[partesRuta.length - 1];
						
						//Comorbamos is el fichero se encuentra en la lista
						nombreValido = this.ComprobarFichero(ficherosCarpeta, ficheroUsuario);
						
						//Enviamos el booleano
						flujoSalida.writeBoolean(nombreValido);
						
						//Le restamos uno a las veces
						veces--;
					}
					//Comrobamos is se ha salido del bucle porque se complio la condición o poruqe se acabaron las veces
					if (veces > 0) 
					{
						//Recibimos el fichero del cliente
						this.RecibirFichero(ficheroUsuario);
					}
				} 
				catch (Exception e) 
				{
					throw new Exception(e.getMessage());
				}
		
	}

	//Método que recibe el fichero del cliente y lo almacena en los ficheros del servidor
	private void RecibirFichero(String ficheroUsuario) throws Exception 
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
					//Añadimos la ruta donde ser va a guardar el fichero
					ruta = "src/ArchivosServidor/" + ficheroUsuario;
					
					//Creamos el buffer con el tamaño del array
					arrFichero = new byte[(int) flujoEntrada.readLong()];
					
					//Obtenemos el fichero recibido
					bos = new BufferedOutputStream(new FileOutputStream(ruta));

					//Leemos los datos
					while(valor < arrFichero.length)
		            {
		                arrFichero[valor] = flujoEntrada.readByte();
		                valor++;
		            }
		            
					//Grabamos los byte en el fichero
					bos.write(arrFichero);
				} 
				catch (Exception e) 
				{
					throw new Exception(e.getMessage());
				}
				finally {
					bos.close();
				}
				
				
		
	}

	//Método que envia un fichero al cliente
	private void EnviarComprobarFichero() throws Exception 
	{
		//Almacenará el nombre de los ficheros de la carpeta
		String[] ficherosCarpeta = null;
		
		//Almacena un booleano que indica que el nombre no es valido
		Boolean nombreValido = false;
		
		//Almacena el nombre del fichero que desea el usuario
		String ficheroUsuario = null;
		
		//El usuario podra introducir hasta 3 nombres
		int veces = 3;
		
		try 
		{
			//Obtenemos la lista de nombres de ficheros
			ficherosCarpeta = server.carpetaFicherosServidor.list();
			
			//Obtenemos el nombre del fichero y comprobamos si se encuentra en el array
			while (!nombreValido && veces > 0) 
			{
				//Almacenamos el fichero del usuairo
				ficheroUsuario = flujoEntrada.readUTF().trim();
				
				//Comorbamos is el fichero se encuentra en la lista
				nombreValido = this.ComprobarFichero(ficherosCarpeta, ficheroUsuario);
				
				//Enviamos el booleano
				flujoSalida.writeBoolean(nombreValido);
				
				//Le restamos uno a las veces
				veces--;
			}
			//Comrobamos is se ha salido del bucle porque se complio la condición o poruqe se acabaron las veces
			if (veces > 0) 
			{
				//Enviamos el fichero
				this.EnviarFichero(ficheroUsuario);
			}
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
	}
	
	//Envia el fichero al cliente
	private void EnviarFichero(String nombreArchivo) throws Exception
	{
		//Almacena el fichero
		File ficheroUsuario;
		
		//Almacenará la cadena de bytes
		byte[] arrFichero = null;
		
		//Flujos para almacenar el fichero
        BufferedInputStream bis = null;
        
        
        //Valor que se lee
        int valor;
        
		try 
		{
			//Obtenemos el fichero
			ficheroUsuario = new File("src/ArchivosServidor/" + nombreArchivo);
			
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
	
	//Comprueba si el fichero se encuentra en el servidor
	//Comprueba si el nombre del fichero se encuentra en un array y devuelve true
	private boolean ComprobarFichero(String[] ficherosCarpeta, String ficheroUsuario) throws Exception 
	{
		try 
		{
			//Recorremos la lista de ficheros
			for (String nombre : ficherosCarpeta) 
			{
				if (nombre.equals(ficheroUsuario)) 
				{
					return true;
				}
			}
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
		return false;
	}
	
	//Método que envia una lista de con los ficheros uqe se encuentran en el servidor
	private void MostrarArchivos() throws Exception 
	{
		//Almacenará el nombre de los ficheros de la carpeta
		String[] ficherosCarpeta = null;
		
		try 
		{
			//Añadimso la lista de ficheros
			ficherosCarpeta = server.carpetaFicherosServidor.list();
			
			//Enviamos la lista
			salidaObjeto.writeObject(ficherosCarpeta);
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
	}
	
	
}
