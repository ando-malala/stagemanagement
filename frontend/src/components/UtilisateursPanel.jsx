import { useCallback, useEffect, useState } from "react";
import { creerUtilisateur,listerUtilisateurs } from "../api/utilisateur/utilisateurApi";

const ROLES = [
    'ADMIN',
    'RESPONSABLE_STAGE',
    'ENCADREUR',
    'STAGIAIRE'
];

export default function UtilisateursPanel({token,onSessionExpiree}){
    const [utilisateurs, setUtilisateurs] = useState([]);
    const [chargement, setChargement] = useState(true);

    
}