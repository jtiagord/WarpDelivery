import ReactMapboxGl,{Layer,Source,Feature} from 'react-mapbox-gl'
import React,{useState,useRef,useEffect} from 'react'
import {Marker} from './marker'

export function Map({token}:{token:string}){
    const [center,setCenter] = useState<Coordinates>({lat:-9.114954,long:38.756651})
    const [zoom, setZoom] = useState(17)

    const Map = ReactMapboxGl({
      accessToken:
        'pk.eyJ1IjoiYTQ0ODE2IiwiYSI6ImNrcmF2YnRmMzFsOHoycGxwb3h1bGswc2sifQ.qJqfMCqIvQ_5ctcCo2v4qg'
    }); 
    
return(
   <div>
    <Map
      style="mapbox://styles/mapbox/streets-v11"
      containerStyle={{
        height: '90vh',
        width: '100vw'
      }}
      center={[center.lat,center.long]}
      zoom={[zoom]}
    >
      <Marker token={token}/>
    </Map>
    </div>
)
}