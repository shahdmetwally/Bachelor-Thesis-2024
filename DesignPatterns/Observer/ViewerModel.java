package net.sourceforge.pmd.util.viewer.model;

import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import java.io.StringReader;
import java.util.List;
import java.util.Vector;


public class ViewerModel
{
  private Vector     listeners;
  private SimpleNode rootNode;
  private List       evaluationResults;

  public ViewerModel(  )
  {
    listeners = new Vector( 5 );
  }

  public SimpleNode getRootNode(  )
  {
    return rootNode;
  }

  public void commitSource( String source )
  {
    ASTCompilationUnit compilationUnit = new TargetJDK1_4().createParser(new StringReader( source )).CompilationUnit(  );
    rootNode = compilationUnit;
    fireViewerModelEvent(
      new ViewerModelEvent( this, ViewerModelEvent.CODE_RECOMPILED ) );
  }

  public boolean hasCompiledTree(  )
  {
    return rootNode != null;
  }

  public void evaluateXPathExpression( String xPath, Object evaluator )
    throws ParseException, JaxenException
  {
    XPath xpath = new BaseXPath( xPath, new DocumentNavigator(  ) );

    evaluationResults = xpath.selectNodes( rootNode );

    fireViewerModelEvent(
      new ViewerModelEvent(
        evaluator, ViewerModelEvent.PATH_EXPRESSION_EVALUATED ) );
  }

  public List getLastEvaluationResults(  )
  {
    return evaluationResults;
  }

  public void selectNode( SimpleNode node, Object selector )
  {
    fireViewerModelEvent(
      new ViewerModelEvent( selector, ViewerModelEvent.NODE_SELECTED, node ) );
  }

  public void appendToXPathExpression( String pathFragment, Object appender )
  {
    fireViewerModelEvent(
      new ViewerModelEvent(
        appender, ViewerModelEvent.PATH_EXPRESSION_APPENDED, pathFragment ) );
  }

  public void addViewerModelListener( ViewerModelListener l )
  {
    listeners.add( l );
  }

  public void removeViewerModelListener( ViewerModelListener l )
  {
    listeners.remove( l );
  }

  protected void fireViewerModelEvent( ViewerModelEvent e )
  {
    for ( int i = 0; i < listeners.size(  ); i++ )
    {
      ( (ViewerModelListener)listeners.elementAt( i ) ).viewerModelChanged( e );
    }
  }
}

