import React,{useState,useRef,useEffect} from 'react'
import {Map} from './map'

export function Home(){
    const input = useRef(null)
    const [isSubmit, setIsSubmit] = useState<string>(null)

    function tokenSubmit(){
        setIsSubmit(input.current)
    }

    function tokenChange(ev:React.ChangeEvent<HTMLInputElement>){
        input.current=ev.currentTarget.value
    }

return(
   <div>
       <input ref={input} onChange={(ev)=>tokenChange(ev)} type='text' />
       <button onClick={tokenSubmit}>Search</button>
       {isSubmit?
       <Map token={isSubmit}/>
       :
       null
       }    
    </div>
)
}