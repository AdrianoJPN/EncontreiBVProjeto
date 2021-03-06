package com.app.encontreibvrr.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.encontreibvrr.R;
import com.app.encontreibvrr.activity.FirebaseHelper;
import com.app.encontreibvrr.activity.MyDatabaseUtil;
import com.app.encontreibvrr.activity.PostDocumentosAchados;
import com.app.encontreibvrr.activity.PostViewHolderDocumentoAchados;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Fragment_Achados extends Fragment {

    public Fragment_Achados() {}
    private String mTitle;

    public static Fragment_Achados newInstance() {
        Fragment_Achados fragment = new Fragment_Achados();
        return fragment;
    }

    private DatabaseReference databaseReference;
    private FirebaseHelper helper;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<PostDocumentosAchados, PostViewHolderDocumentoAchados> mAdapter;
    private static final String TAG = "FragmentAchados";

    static boolean calledAlready = false;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_achados, container, false);

        //A FirebaseApp é inicializado por um ContentProvider por isso não é inicializado no
        // momento onCreate () é chamado. Em seguida, chamar Utils.getDatabase () de qualquer atividade
        // de usar o banco de dados. Obtenha seu FirebaseDatabase como este: método de classe
        MyDatabaseUtil.getDatabase();

        // Aplicativos Firebase tratar automaticamente as interrupções temporárias da rede para você.
        // dados em cache ainda estará disponível enquanto estiver offline e suas gravações será
        // reenviado quando a conectividade de rede é recuperada . Permitindo a persistência de disco
        // permite que nosso aplicativo também para manter todos os seus estados , mesmo após uma
        // reinicialização do aplicativo. Podemos permitir que a persistência de disco com apenas uma
        // linha de código .
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Verificado no site do Firebase Blog - Analisar
        //Firebase.getDefaultConfig().setPersistenceEnabled(true);

        //INITIALIZE FIREBASE DB
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //databaseReference.push();
        helper = new FirebaseHelper(databaseReference);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Ppedaço de código em nosso aplicativo que consulta para os últimos quatro itens na nossa base
        // de dados Firebase Realtime de partituras
        //databaseReference = FirebaseDatabase.getInstance().getReference("Spacecraft");

        //O banco de dados Firebase Realtime sincroniza e armazena uma cópia local dos dados para
        // ouvintes ativos . Além disso, você pode manter ActivityLocais específicos em sincronia .
        //databaseReference.keepSynced(true);
        databaseReference.keepSynced(true);


        //SETUP RECYCLER
        mRecycler = (RecyclerView) view.findViewById(R.id.rv);
        mRecycler.setHasFixedSize(true);




        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getContext().getApplicationContext());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        //Query postsQuery = getQuery(mDatabase);
        //Query postsQuery = databaseReference.child("Spacecraft").child(myUserId).orderByChild("starCount");
        Query postsQuery = databaseReference.child("DocumentosAchados");

        mAdapter = new FirebaseRecyclerAdapter<
                PostDocumentosAchados,
                PostViewHolderDocumentoAchados>(

                PostDocumentosAchados.class, R.layout.item_recycler,
                PostViewHolderDocumentoAchados.class, postsQuery) {

            @Override
            protected void populateViewHolder(final PostViewHolderDocumentoAchados viewHolder,
                                              final PostDocumentosAchados model, final int position) {

                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        //Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                        //intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        //startActivity(intent);
                    }
                });

                //Efeito animate
                try {
                    YoYo.with(Techniques.BounceIn)
                            .duration(500)
                            .playOn(viewHolder.itemView);
                } catch (Exception e) {

                }



                // Show progress bar
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                // Hide progress bar on successful load
                // .networkPolicy(NetworkPolicy.OFFLINE)
                Log.d("Picasso", model.imagemurl);
                if (model.imagemurl.equals("NENHUMA")){
                    viewHolder.frameLayout.setVisibility(View.GONE);
                } else {
                    Picasso.with(getActivity().getApplicationContext()).load(model.imagemurl)
                            .into(viewHolder.ivBasicImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    if (viewHolder.progressBar != null) {
                                        viewHolder.progressBar.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onError() {

                                }
                            });
                }


                /*
                // Show progress bar
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                // Hide progress bar on successful load
                // .networkPolicy(NetworkPolicy.OFFLINE)
                Log.d("Picasso", model.imagemurl);
                if (model.imagemurl.equals("NENHUMA")){
                    viewHolder.frameLayout.setVisibility(View.GONE);
                } else {
                    Picasso.with(getActivity().getApplicationContext())
                            .downloader(new OkHttpDownloader(getContext(), Integer.MAX_VALUE))
                            .load(model.imagemurl)
                            .into(viewHolder.ivBasicImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    if (viewHolder.progressBar != null) {
                                        viewHolder.progressBar.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onError() {

                                }
                            });
                }
                */

                /*
                // Show progress bar
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                // Hide progress bar on successful load
                // .networkPolicy(NetworkPolicy.OFFLINE)
                Log.d("Picasso", model.imagemurl);
                if (model.imagemurl.equals("NENHUMA")){
                    viewHolder.frameLayout.setVisibility(View.GONE);
                } else {
                    Picasso.with(getActivity())
                            .load(model.imagemurl)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(viewHolder.ivBasicImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    if (viewHolder.progressBar != null) {
                                        viewHolder.progressBar.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onError() {

                                }
                            });
                }
                */


                /*
                // Show progress bar
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                // Hide progress bar on successful load
                // .networkPolicy(NetworkPolicy.OFFLINE)
                Log.d("Picasso", model.imagemurl);
                if (model.imagemurl.equals("NENHUMA")){
                    viewHolder.frameLayout.setVisibility(View.GONE);
                } else {
                    Picasso.with(getActivity())
                            .load(model.imagemurl)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .error(android.R.drawable.sym_contact_card)
                            .into(viewHolder.ivBasicImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    if (viewHolder.progressBar != null) {
                                        viewHolder.progressBar.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onError() {

                                }
                            });
                }
                */





                // Bind Post to ViewHolder, setting OnClickListener for the star button
                // Bind Publicar ViewHolder , estabelecendo OnClickListener para o botão de estrela
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        // Necessidade de escrever para ambos os lugares o post é armazenado
                        //DatabaseReference globalPostRef = databaseReference.child("Spacecraft").child(postRef.getKey());
                        //DatabaseReference userPostRef = databaseReference.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Run two transactions
                        // Executar duas operações
                        //onStarClicked(globalPostRef);
                        //onStarClicked(userPostRef);
                    }
                });
            }
        };

        mRecycler.setAdapter(mAdapter);






        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

}
