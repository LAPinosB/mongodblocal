package mongodblocal;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class javamongo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Ruta de mi base de datos Local
		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		System.out.println("Mongo coneccion creada correctamente.");
		// Nombre de la base de datos que puede existir o no, si no existe lo crea con getDatabase (Linea 30)
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

		// Añadir valores a la colección
		Document document = new Document("Nombre", "Ash").append("Edad", 12).append("Ciudad", "Ciudad Esmeralda")
				.append("SSCC", 1234567890);
		collection.insertOne(document);
		System.out.println("Valores añadidos correctamente a la colección.");
	}

}
