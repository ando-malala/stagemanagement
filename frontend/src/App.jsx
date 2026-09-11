import { useState, useEffect } from "react";
import { getTypeAlerte } from "./api/alerte/alerteApi";

function App(){
  const [typeAlerte, setTypeAlerte] = useState([]);

  useEffect(()=>{
      async function loadTypeAlerte() {
        const liste = await getTypeAlerte();
        setTypeAlerte(liste);
      }

      loadTypeAlerte();
  },[])

  return(
    <>
      {typeAlerte.map((type) => (
        <p key={type.id}>
            {type.libelle}
        </p>
      ))}
    </>
  )
}

export default App