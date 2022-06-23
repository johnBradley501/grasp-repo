package uk.ac.kcl.cch.jb.sparql.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.gef.LayerConstants;

// modelled after Pliny's "RootFigure"   JB
// see also Pliny organiser's WorkBenchHolderFigure

public class WhereClauseRootFigure extends ScrollPane {
	private ConnectionLayer myConnectionLayer;
	private IFigure contentsLayer;
	
	public WhereClauseRootFigure() {
		super();
		FreeformLayeredPane innerLayers = new FreeformLayeredPane();
    	contentsLayer = new FreeformLayer();
    	contentsLayer.setLayoutManager(new FreeformLayout());
		contentsLayer.setBorder(new LineBorder(1));

    	myConnectionLayer = new ConnectionLayer();

    	innerLayers.add(myConnectionLayer, LayerConstants.CONNECTION_LAYER, -1);
    	innerLayers.add(contentsLayer, null, -1);
    	
    	setContents(innerLayers);

		FreeformViewport myViewPort = new FreeformViewport();
		myViewPort.setContents(innerLayers);
		setViewport(myViewPort);
	}
	
	public ConnectionLayer getMyConnectionLayer(){
		return myConnectionLayer;
	}

	public IFigure getContentsFigure(){
		return contentsLayer;
	}

}
