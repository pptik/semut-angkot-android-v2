package id.pptik.semutangkot.fragments.map;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import id.pptik.semutangkot.R;
import id.pptik.semutangkot.helper.map.osm.MapUtilities;
import id.pptik.semutangkot.helper.map.osm.MarkerClick;
import id.pptik.semutangkot.helper.map.osm.OsmMarker;
import id.pptik.semutangkot.models.Cctv;
import id.pptik.semutangkot.ui.AnimationView;


public class CctvMapFragment extends Fragment implements Marker.OnMarkerClickListener {

    private ArrayList<Cctv> list = new ArrayList<Cctv>();
    private MapView mMapView;
    private MapUtilities mapUitilities;
    private OsmMarker osmMarker;
    private IMapController mapController;
    private MarkerClick markerClick;
    private RelativeLayout markerDetailLayout;
    private Animation slideDown;

    public CctvMapFragment(){

    }

    public void setData(ArrayList<Cctv> list){
        this.list = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View convertView = inflater.inflate(R.layout.fragment_cctv_map, container, false);
        mMapView = convertView.findViewById(R.id.maposm);
        markerDetailLayout = convertView.findViewById(R.id.markerdetail_layout);

        markerClick = new MarkerClick(getActivity(), markerDetailLayout);

        mapUitilities = new MapUtilities(mMapView);
        osmMarker = new OsmMarker(mMapView);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setMultiTouchControls(true);
        mapController = mMapView.getController();
        mapController.setZoom(25);

        for(int i = 0; i < list.size(); i++){
            Marker marker = osmMarker.add(list.get(i));
            marker.setOnMarkerClickListener(this);
            mapController.animateTo(marker.getPosition());
        }

        ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        for(int i = 0; i < mMapView.getOverlays().size(); i++){
            if(mMapView.getOverlays().get(i) instanceof Marker ) {
                Marker marker = (Marker) mMapView.getOverlays().get(i);
                geoPoints.add(marker.getPosition());
            }
        }

        zoomToBounds(MapUtilities.computeArea(geoPoints));

        slideDown = new AnimationView(getActivity()).getAnimation(R.anim.slide_down, anim -> {
            if(markerDetailLayout.getVisibility() == View.VISIBLE) markerDetailLayout.setVisibility(View.GONE);
        });

        markerDetailLayout.setOnClickListener(view -> markerDetailLayout.startAnimation(slideDown));
        return convertView;
    }


    public void zoomToBounds(final BoundingBox box) {
        if (mMapView.getHeight() > 0) {
            mMapView.zoomToBoundingBox(box, true);
        } else {
            ViewTreeObserver vto = mMapView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mMapView.zoomToBoundingBox(box, true);
                    ViewTreeObserver vto2 = mMapView.getViewTreeObserver();
                    vto2.removeOnGlobalLayoutListener(this);

                }
            });
        }
    }






    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        markerClick.checkMarker(marker);
        return false;
    }
}