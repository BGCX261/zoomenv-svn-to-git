package test.cdo.client1;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;

import library.Author;
import library.Library;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import java.io.IOException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EStructuralFeature;
import library.LibraryPackage.Literals;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.emf.cdo.util.CommitException;

public class ListAuthorViewPart extends ViewPart {
	private DataBindingContext m_bindingContext;
	private Table table;
	
	private Library library; //The library instance
    private CDOSession cdoSession; //The CDO session
    private TableViewer tableViewer;
    private Text txtName;
    private WritableValue author = new WritableValue();
    protected CDOTransaction transaction;
    
	public ListAuthorViewPart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		
		//Open CDO session and view
        cdoSession = TestCdoClient.openSession("demo");
        CDOView view = cdoSession.openView();
        view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
        CDOResource resource = view.getResource("/myResource");
        try {
            //Load resource library
            resource.load(null);
            library = (Library) resource.getContents().get(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		parent.setLayout(new GridLayout(1, false));
		
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (transaction!=null) {
                    transaction.rollback();
                }
                transaction = cdoSession.openTransaction();
                Author authorSelected = (Author) ((StructuredSelection) event.getSelection()).getFirstElement();
                Author authorTransactionalObject = (Author) transaction.getObject(authorSelected);
                author.setValue( authorTransactionalObject );
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumn.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblName = new Label(composite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name");
		
		txtName = new Text(composite, SWT.BORDER);
		txtName.setText("Name");
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSave = new Button(composite, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
	                transaction.commit();
	            } catch (CommitException e1) {
	                e1.printStackTrace();
	            }
			}
		});
		btnSave.setText("Save");
		new Label(composite, SWT.NONE);
		m_bindingContext = initDataBindings();
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableMap[] observeMaps = EMFObservables.observeMaps(listContentProvider.getKnownElements(), new EStructuralFeature[]{Literals.AUTHOR__NAME});
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		//
		IObservableList libraryListAuthorObserveList = EMFObservables.observeList(Realm.getDefault(), library, Literals.LIBRARY__LIST_AUTHOR);
		tableViewer.setInput(libraryListAuthorObserveList);
		//
		IObservableValue txtNameObserveTextObserveWidget = SWTObservables.observeText(txtName, SWT.Modify);
		IObservableValue authorNameObserveDetailValue = PojoObservables.observeDetailValue(author, "name", String.class);
		bindingContext.bindValue(txtNameObserveTextObserveWidget, authorNameObserveDetailValue, null, null);
		//
		return bindingContext;
	}
}
