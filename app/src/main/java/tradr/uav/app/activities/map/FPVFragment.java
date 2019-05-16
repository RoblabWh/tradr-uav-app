package tradr.uav.app.activities.map;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import dji.sdk.codec.DJICodecManager;
import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.uav.UAVLiveStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FPVFragment.InteractionListener} interface
 * to handle interaction events.
 * Use the {@link FPVFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FPVFragment extends Fragment {


    private InteractionListener listener;

    private TextureView videoSurface;

    private View view;

    private UAV uav;

    private int videoWidth;
    private int videoHeight;

    private DJICodecManager codecManager;

    public FPVFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters
     */
    // TODO: Rename and change types and number of parameters
    public static FPVFragment newInstance() {
        FPVFragment fragment = new FPVFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.uav = UavApplication.getUav();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_fpv, container, false);

        initUI();

        return this.view;

    }

    private void initUI() {
        videoSurface     = (TextureView)  this.view.findViewById(R.id.video_previewer_surface);


    }

    private void initVideoSurface() {

        if (null != videoSurface) {
            videoSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    on_videoSurface_available(surface, width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    on_videoSurface_sizeChanged(surface, width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return on_videoSurface_destroyed(surface);
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    on_videoSurface_updated(surface);
                }
            });
        }

    }

    private void on_videoSurface_available(SurfaceTexture surface, int width, int height) {
        registerSurface(surface, width, height);
    }

    private void on_videoSurface_sizeChanged(SurfaceTexture surface, int width, int height) {
        registerSurface(surface, width, height);
    }

    private boolean on_videoSurface_destroyed(SurfaceTexture surface) {
        unregisterSurface();
        return false;
    }

    private void on_videoSurface_updated(SurfaceTexture surface) {
        if (this.videoHeight != codecManager.getVideoHeight() ||
            this.videoWidth != codecManager.getVideoWidth()) {

            this.videoWidth = codecManager.getVideoWidth();
            this.videoHeight = codecManager.getVideoHeight();

            adjustAspectRatio();
        }
    }

    private void adjustAspectRatio() {

        int viewWidth = this.videoSurface.getWidth();
        int viewHeight = this.videoSurface.getHeight();

        double aspectRatio = ((double) this.videoHeight) / ((double) this.videoWidth);

        int newWidth;
        int newHeight;

        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }

        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;

        Matrix txform = new Matrix();
        this.videoSurface.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        txform.postTranslate(xoff, yoff);
        this.videoSurface.setTransform(txform);
    }

    private void registerSurface(SurfaceTexture surface, int width, int height) {
        unregisterSurface();
        Log.d("Surface", "width: " + width + "    height: " + height);
        codecManager = new DJICodecManager(this.getContext(), surface, width, height);
    }

    private void unregisterSurface() {
        if (codecManager != null) {
            codecManager.cleanSurface();
            codecManager = null;
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private UAVLiveStream.H264Listener liveStreamListener = new UAVLiveStream.H264Listener() {
        @Override
        public void onDataReceived(byte[] data, int size) {
            if (codecManager != null) {
                codecManager.sendDataToDecoder(data, size);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        initVideoSurface();

        if (this.uav.isAircraftConnected()) {
            this.uav.liveStream.addH264Listener(this.liveStreamListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (uav != null && uav.liveStream != null) {
            this.uav.liveStream.removeH264Listener(this.liveStreamListener);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface InteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
