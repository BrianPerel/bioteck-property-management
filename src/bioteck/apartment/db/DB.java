package bioteck.apartment.db;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Required class to build a session for the database
 *
 * @version 3
 */
public class DB {
	private static final DB db = new DB();

	private SessionFactory sessionFactory = null;

	private DB() {
		setup();
	}

	private void setup() throws HibernateException {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			StandardServiceRegistryBuilder.destroy(registry);
			throw new HibernateException("Config Error.");
		}
	}

	public static Session openSession() {
		return db.sessionFactory.openSession();
	}

	public static void transact(ITr tt) {
		Session s = DB.openSession();
		Transaction t = null;

		try {
			t = s.beginTransaction();
			tt.process(s);
			t.commit();
		} catch (Exception e) {
			t.rollback();
		} finally {
			s.close();
		}
	}

	public static <T> List<T> query(IQuery<T> q) {
		Session s = DB.openSession();
		List<T> output = new ArrayList<>();

		try {
			output = q.process(s);
		} catch (Exception e) {
			System.out.println("The requested Query did not work.");
		} finally {
			s.close();
		}
		return output;
	}
}
