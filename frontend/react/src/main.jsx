import React from 'react'
import ReactDOM from 'react-dom/client'
import { ChakraProvider, Text } from '@chakra-ui/react'
import { createStandaloneToast } from '@chakra-ui/react'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./components/login/Login.jsx";
import Signup from "./components/signup/Signup.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./components/shared/ProtectedRoute.jsx";
import Customer from "./Customer.jsx";
import './index.css'
import Home from "./Home.jsx";

const { ToastContainer } = createStandaloneToast()

const router = createBrowserRouter([
    {
        path:"/",
        element: <Login/>
    },
    {
        path:"/signup",
        element: <Signup/>
    },
    {
        path: "dashboard/customers",
        element: <ProtectedRoute><Customer/></ProtectedRoute>
    },
    {
        path: "dashboard",
        element: <ProtectedRoute><Home/></ProtectedRoute>
    }
])

ReactDOM
    .createRoot(document.getElementById('root'))
    .render(
        <React.StrictMode>
            <ChakraProvider>
                <AuthProvider>
                    <RouterProvider router={router} />
                        </AuthProvider>
                    <ToastContainer/>
            </ChakraProvider>
        </React.StrictMode>,
    )