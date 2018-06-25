
package MODEL;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Los objetos de esta clase son hilos que al correr escuchan permanentemente lo
 * que los clientes puedan decir, hay un hilo para cada cliente que se conecta
 * al servidor y dicho hilo tiene como funci�n escuchar solamente a ese cliente.
 * 
 * @author Erick Navarro
 */
public class HiloCliente extends Thread {
	/**
	 * Socket que se utiliza para comunicarse con el cliente.
	 */
	private final Socket socket;
	/**
	 * Stream con el que se env�an objetos al servidor.
	 */
	private ObjectOutputStream objectOutputStream;
	/**
	 * Stream con el que se reciben objetos del servidor.
	 */
	private ObjectInputStream objectInputStream;
	/**
	 * Servidor al que pertenece este hilo.
	 */
	private final Servidor server;
	/**
	 * Identificador �nico del cliente con el que este hilo se comunica.
	 */
	private String identificador;
	/**
	 * Variable booleana que almacena verdadero cuando este hilo esta escuchando
	 * lo que el cliente que atiende esta diciendo.
	 */
	private boolean escuchando;

	/**
	 * M�todo constructor de la clase hilo cliente.
	 * 
	 * @param socket
	 * @param server
	 */
	public HiloCliente(Socket socket, Servidor server) {
		this.server = server;
		this.socket = socket;
		try {
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException ex) {
			System.err.println("Error en la inicializaci�n del ObjectOutputStream y el ObjectInputStream");
		}
	}

	/**
	 * M�todo encargado de cerrar el socket con el que se esta comunicando.
	 */
	public void desconnectar() {
		try {
			socket.close();
			escuchando = false;
		} catch (IOException ex) {
			System.err.println("Error al cerrar el socket de comunicaci�n con el cliente.");
		}
	}

	/**
	 * Sobreescritura del m�todo de Thread, es ac� en donde se monta el ciclo
	 * infinito.
	 */
	public void run() {
		try {
			escuchar();
		} catch (Exception ex) {
			System.err.println("Error al llamar al m�todo readLine del hilo del cliente.");
		}
		desconnectar();
	}

	/**
	 * M�todo que constantemente esta escuchando todo lo que es enviado por el
	 * cliente que se comunica con �l.
	 */
	public void escuchar() {
		escuchando = true;
		while (escuchando) {
			try {
				Object aux = objectInputStream.readObject();
				if (aux instanceof LinkedList) {
					ejecutar((LinkedList<String>) aux);
				}
			} catch (Exception e) {
				System.err.println("Error al leer lo enviado por el cliente.");
			}
		}
	}

	/**
	 * M�todo que realiza determinadas acciones dependiendo de lo que el socket
	 * haya recibido y lo que este le envie el m�todo, en �l se manejan una
	 * serie de c�digos.
	 * 
	 * @param lista
	 */
	public void ejecutar(LinkedList<String> lista) {
		// 0 - El primer elemento de la lista es siempre el tipo
		String tipo = lista.get(0);
		switch (tipo) {
		case "SOLICITUD_CONEXION":
			// 1 - Identificador propio del nuevo usuario
			confirmarConexion(lista.get(1));
			break;
		case "SOLICITUD_DESCONEXION":
			// 1 - Identificador propio del nuevo usuario
			confirmarDesConexion();
			break;
		case "MENSAJE":
			// 1 - Cliente emisor
			// 2 - Cliente receptor
			// 3 - Mensaje
			String destinatario = lista.get(2);
			server.clientes.stream().filter(h -> (destinatario.equals(h.getIdentificador())))
					.forEach((h) -> h.enviarMensaje(lista));
			break;
		case "JUGADOR":
			String destina = lista.get(2);
			server.clientes.stream().filter(h -> (destina.equals(h.getIdentificador())))
					.forEach((h) -> h.enviarMensaje(lista));
			break;
		case "JUGADOR2":
			String destina2 = lista.get(2);
			server.clientes.stream().filter(h -> (destina2.equals(h.getIdentificador())))
					.forEach((h) -> h.enviarMensaje(lista));
			break;
		case "PUNTAJE1":
			String destina4 = lista.get(2);
			server.clientes.stream().filter(h -> (destina4.equals(h.getIdentificador())))
					.forEach((h) -> h.enviarMensaje(lista));
			break;
		case "PUNTAJE2":
			String destina3 = lista.get(2);
			server.clientes.stream().filter(h -> (destina3.equals(h.getIdentificador())))
					.forEach((h) -> h.enviarMensaje(lista));
			break;
		case "SAQUE":
			String destina5 = lista.get(2);
			server.clientes.stream().filter(h -> (destina5.equals(h.getIdentificador())))
					.forEach((h) -> h.enviarMensaje(lista));
			break;

		default:
			break;
		}
	}

	/**
	 * M�todo para enviar un mensaje al cliente atraves del socket.
	 * 
	 * @param lista
	 */
	private void enviarMensaje(LinkedList<String> lista) {
		try {
			objectOutputStream.writeObject(lista);
		} catch (Exception e) {
			System.err.println("Error al enviar el objeto al cliente.");
		}
	}

	/**
	 * Una vez conectado un nuevo cliente, este m�todo notifica a todos los
	 * clientes conectados que hay un nuevo cliente para que lo agreguen a sus
	 * contactos.
	 * 
	 * @param identificador
	 */
	private void confirmarConexion(String identificador) {
		Servidor.correlativo++;
		this.identificador = Servidor.correlativo + " - " + identificador;
		LinkedList<String> lista = new LinkedList<>();
		lista.add("CONEXION_ACEPTADA");
		lista.add(this.identificador);
		lista.addAll(server.getUsuariosConectados());
		enviarMensaje(lista);
		server.agregarLog("\nNuevo cliente: " + this.identificador);
		// enviar a todos los clientes el nombre del nuevo usuario conectado
		// excepto a �l mismo
		LinkedList<String> auxLista = new LinkedList<>();
		auxLista.add("NUEVO_USUARIO_CONECTADO");
		auxLista.add(this.identificador);
		server.clientes.stream().forEach(cliente -> cliente.enviarMensaje(auxLista));
		server.clientes.add(this);
	}

	/**
	 * M�todo que retorna el identificador �nico del cliente dentro del chat.
	 * 
	 * @return
	 */
	public String getIdentificador() {
		return identificador;
	}

	/**
	 * M�todo que se invoca cuando el usuarioi al que atienede este hilo decide
	 * desconectarse, si eso pasa, se tiene que informar al resto de los
	 * usuarios que ya no pueden enviarle mensajes y que deben quitarlo de su
	 * lista de contactos.
	 */
	private void confirmarDesConexion() {
		LinkedList<String> auxLista = new LinkedList<>();
		auxLista.add("USUARIO_DESCONECTADO");
		auxLista.add(this.identificador);
		server.agregarLog("\nEl cliente \"" + this.identificador + "\" se ha desconectado.");
		this.desconnectar();
		for (int i = 0; i < server.clientes.size(); i++) {
			if (server.clientes.get(i).equals(this)) {
				server.clientes.remove(i);
				break;
			}
		}
		server.clientes.stream().forEach(h -> h.enviarMensaje(auxLista));
	}
}