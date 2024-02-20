package mongodblocal;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class javamongo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Dentro del try se cierra automaticamente no hay que poner close en gneral.
		// Ruta de mi base de datos Local
		// Curiosidad de new MongoClient("localhost", 27017) link abajo
		// https://stackoverflow.com/questions/60267087/differences-between-com-mongodb-client-mongoclient-and-com-mongodb-mongoclient
		try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
			int sscCandidato = 1234567890; // Esto representa el SSCC a insertar
			System.out.println("Mongo coneccion creada correctamente.");
			// Nombre de la base de datos que puede existir o no, si no existe lo crea con
			// getDatabase (Linea 30)
			String databaseName = "mibasededatosluis";
			// Comprobar si la base de datos "mibasededatosluis" existe
			if (mongoClient.listDatabaseNames().into(new ArrayList<>()).contains(databaseName)) {
				System.out.println("La base de datos 'mibasededatosluis' ya existe.");
			} else {
				System.out.println("La base de datos 'mibasededatosluis' no existe.");
			}

			// Obtener la base de datos, sino existe la crea.
			MongoDatabase database = mongoClient.getDatabase(databaseName);
			System.out.println("Base de datos '" + databaseName + "' obtenida correctamente.");

			// Verificar si la colección existe
			String collectionName = "micoleccion";
			MongoCollection<Document> collection = database.getCollection(collectionName);

			if (collection != null) {
				System.out.println("La colección '" + collectionName + "' ya existe.");
			} else {
				// Crear la colección
				database.createCollection(collectionName);
				System.out.println("La colección '" + collectionName + "' ha sido creada.");
			}

			try {
				// Crear el índice único para el campo "SSCC" SI ESTO DEJO FUERA ME SALTA EL
				// ERROR DEL CATCH Y NO ME HACE LO DEMÁS.
				collection.createIndex(Indexes.ascending("SSCC"), new IndexOptions().unique(true));
				// Añadir valores a la colección

				if (validarSSCC(sscCandidato)) {
					Document document = new Document("Nombre", "Ash").append("Edad", 12)
							.append("Ciudad", "Ciudad Esmeralda").append("SSCC", sscCandidato);
					collection.insertOne(document);
					System.out.println("Valores añadidos correctamente a la colección.");
				} else {
					System.out.println("El SSCC no cumple con los requisitos.");
				}

			} catch (DuplicateKeyException e) {
				// Si se produce una excepción de clave duplicada,controlamos el mensaje de
				// error.
				System.out.println("El valor SSCC ya existe en la colección.");
				// e.printStackTrace(); Si saco este try fuera, no me va a ejecutar los
				// siguiebntes apartados.
			}
			
			
			// Definir el filtro para identificar el documento que se va a actualizar
			Document filtroUpdate= new Document("SSCC", sscCandidato);

			// Definir la actualización que se aplicará al documento
			Document actualizacion = new Document("$set", new Document("Nombre", "brock"));

			// Ejecutar la operación de actualización
			UpdateResult resultUpdate = collection.updateOne(filtroUpdate, actualizacion);

			// Obtener el número de documentos modificados
			long documentosModificados = resultUpdate.getModifiedCount();

			// Imprimir un mensaje según el resultado
			if (documentosModificados > 0) {
			    System.out.println("Se actualizó correctamente el documento con el SSCC: " + sscCandidato);
			} else {
			    System.out.println("No se encontró ningún documento con el SSCC: " + sscCandidato);
			}

			// Borrar todos los documentos que coincidan con el SSCC dado
			Document filtro = new Document("SSCC", sscCandidato);
			DeleteResult result = collection.deleteMany(filtro);
			long documentosBorrados = result.getDeletedCount();
			//Si ejecutamos esto y te sale que te elimina 1 es porque antes estamos creando, y es el que elimina.
			if (documentosBorrados > 0) {
				System.out.println("Se eliminaron " + documentosBorrados + " documentos con el sscc especificado: "
						+ sscCandidato);
			} else {
				System.out.println("No se encontraron documentos con el sscc especificado: " + sscCandidato);
			}
			// Consulta todos los documentos en la colección
			try (MongoCursor<Document> cursor = collection.find().iterator()) {
				int count = 0;
				while (cursor.hasNext()) {
					System.out.println(cursor.next().toJson());
					count++;
				}
				if (count == 0) {
					System.out.println("La colección está vacía.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private static boolean validarSSCC(int sscCandidato) {
		// TODO Auto-generated method stub
		String sscStr = String.valueOf(sscCandidato);
		return sscStr.matches("\\d{10}");
	}

}
