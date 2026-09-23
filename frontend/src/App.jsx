import { useCallback,useState, useEffect } from "react";
import { login, logout, me } from './api/auth/authApi.js';
import UtilisateursPanel from './components/UtilisateursPanel.jsx';
import './App.css';

export default function App(){
    const [token,setToken] = useState(null);
    const [utilisateur,setUtilisateur] = useState(null);

    const [identifiant,setIdentifiant] = useState('');
    const [motDePasse, setMotDePasse] = useState('');

    const [enCours, setEnCours] = useState(false);
    const [erreur, setErreur] = useState('');

    const viderSession = useCallback
}