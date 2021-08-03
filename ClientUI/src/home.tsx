import React,{useState,useRef,useEffect} from 'react'
import {Map} from './map'

export function Home(){
    const input = useRef(null)
    const [isSubmit, setIsSubmit] = useState(true)

    function tokenSubmit(){
        setIsSubmit(false)
    }

    function tokenChange(ev:React.ChangeEvent<HTMLInputElement>){
        input.current=ev.currentTarget.value
    }

return(
   <div>
       <input ref={input} onChange={(ev)=>tokenChange(ev)} type='text' />
       <button onClick={tokenSubmit}>Search</button>
       {isSubmit?
       null
       :
       <Map token={input.current}/>
       }    
    </div>
)
}