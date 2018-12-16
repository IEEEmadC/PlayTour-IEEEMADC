/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ir.imandroid.mpmleadertour.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.parceler.Parcel;

@Parcel
public class Pin implements ClusterItem {
    private  String title;
    private  String info;
    private  int photo;
    private  LatLng latLng;
    private String sound;
    private double distnace;
    private String id;
    LeaderTour leaderTour;

    Pin(){}

    public Pin(String id ,LatLng position, String name,String info, int pictureResource,String sound) {
        this.id = id;
        this.title = name;
        this.photo = pictureResource;
        this.latLng = position;
        this.info = info;
        this.sound = sound;
    }

    public double getDistnace() {
        return distnace;
    }

    public void setDistnace(double distnace) {
        this.distnace = distnace;
    }

    public String getInfo() {
        return info;
    }

    public int getPhoto() {
        return photo;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LeaderTour getLeaderTour() {
        return leaderTour;
    }

    public void setLeaderTour(LeaderTour leaderTour) {
        this.leaderTour = leaderTour;
    }
}
