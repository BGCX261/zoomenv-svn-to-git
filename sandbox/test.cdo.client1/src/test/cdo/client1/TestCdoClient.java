package test.cdo.client1;

import library.Author;
import library.Book;
import library.Library;
import library.LibraryFactory;
 
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.junit.BeforeClass;
import org.junit.Test;
 
public class TestCdoClient {
 
    private static CDOSession cdoSession;
 
    @BeforeClass
    public static void init() {
        //The following lines are not needed if the extension
        //registry (OSGi/Equinox) is running
        Net4jUtil.prepareContainer(IPluginContainer.INSTANCE);
        TCPUtil.prepareContainer(IPluginContainer.INSTANCE);
 
        cdoSession = openSession("demo");
    }
 
    @Test
    public void popola() {
        try {
            CDOTransaction transaction = cdoSession.openTransaction();
            CDOResource resource = transaction.getOrCreateResource("/myResource");
            Library library = LibraryFactory.eINSTANCE.createLibrary();
 
            Book book = LibraryFactory.eINSTANCE.createBook();
            book.setTitle("Eclipse Modeling Framework (2nd edition)");
            library.getListBook().add(book);
 
            Author author = LibraryFactory.eINSTANCE.createAuthor();
            author.setName("Ed");
            library.getListAuthor().add(author);
            book.getAuthor().add(author);
 
            author = LibraryFactory.eINSTANCE.createAuthor();
            author.setName("Marcelo");
            library.getListAuthor().add(author);
            book.getAuthor().add(author);
 
            author = LibraryFactory.eINSTANCE.createAuthor();
            author.setName("Frank");
            library.getListAuthor().add(author);
            book.getAuthor().add(author);
 
            author = LibraryFactory.eINSTANCE.createAuthor();
            author.setName("David");
            library.getListAuthor().add(author);
            book.getAuthor().add(author);
 
            resource.getContents().add(library);
            transaction.commit();
            cdoSession.close();
 
        } catch (CommitException e) {
            e.printStackTrace();
        } finally {
            cdoSession.close();
        }
    }
 
    public static CDOSession openSession(String repoName) {
        final IConnector connector = (IConnector) IPluginContainer.INSTANCE
                .getElement( //
                        "org.eclipse.net4j.connectors", // Product group
                        "tcp", // Type
                        "localhost"); // Description
 
        CDOSessionConfiguration config = CDONet4jUtil
                .createSessionConfiguration();
        config.setConnector(connector);
        config.setRepositoryName(repoName);
 
        CDOSession session = config.openSession();
 
        session.addListener(new LifecycleEventAdapter() {
            @Override
            protected void onDeactivated(ILifecycle lifecycle) {
                connector.close();
            }
        });
 
        return session;
    }
 
}
