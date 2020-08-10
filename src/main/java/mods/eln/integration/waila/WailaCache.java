package mods.eln.integration.waila;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import mods.eln.Eln;
import mods.eln.misc.Coordinate;
import mods.eln.packets.GhostNodeWailaRequestPacket;
import mods.eln.packets.SixNodeWailaRequestPacket;
import mods.eln.packets.TransparentNodeRequestPacket;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gregory Maddra on 2016-06-29.
 */
public class WailaCache {

    public static LoadingCache<Coordinate, Map<String, String>> nodes = CacheBuilder.newBuilder()
        .maximumSize(20)
        .refreshAfterWrite(2, TimeUnit.SECONDS)
        .build(
            new CacheLoader<Coordinate, Map<String, String>>() {
                public Map<String, String> load(Coordinate key) throws Exception {
                    Eln.elnNetwork.sendToServer(new TransparentNodeRequestPacket(key));
                    return null;
                }

                @Override
                public ListenableFuture<Map<String, String>> reload(Coordinate key,
                                                                    Map<String, String> oldValue) throws Exception {
                    load(key);
                    return Futures.immediateFuture(oldValue);
                }
            }
        );

    public static LoadingCache<SixNodeCoordinate, SixNodeWailaData> sixNodes = CacheBuilder.newBuilder()
        .maximumSize(20)
        .refreshAfterWrite(2, TimeUnit.SECONDS)
        .build(
            new CacheLoader<SixNodeCoordinate, SixNodeWailaData>() {
                public SixNodeWailaData load(SixNodeCoordinate key) throws Exception {
                    Eln.elnNetwork.sendToServer(new SixNodeWailaRequestPacket(key.getCoord(), key.getSide()));
                    return null;
                }

                @Override
                public ListenableFuture<SixNodeWailaData> reload(SixNodeCoordinate key,
                                                                 SixNodeWailaData oldValue) throws Exception {
                    load(key);
                    return Futures.immediateFuture(oldValue);
                }
            }
        );

    public static LoadingCache<Coordinate, GhostNodeWailaData> ghostNodes = CacheBuilder.newBuilder()
        .maximumSize(20)
        .refreshAfterWrite(10, TimeUnit.SECONDS)
        .build(
            new CacheLoader<Coordinate, GhostNodeWailaData>() {
                public GhostNodeWailaData load(Coordinate key) throws Exception {
                    Eln.elnNetwork.sendToServer(new GhostNodeWailaRequestPacket(key));
                    return null;
                }

                @Override
                public ListenableFuture<GhostNodeWailaData> reload(Coordinate key,
                                                                   GhostNodeWailaData oldValue) throws Exception {
                    load(key);
                    return Futures.immediateFuture(oldValue);
                }
            }
        );

}
