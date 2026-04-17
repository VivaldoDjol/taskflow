import React, { useState } from "react";
import { Button, Card, Input, Spacer } from "@nextui-org/react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { authApi } from "../api/authApi";
import { TokenStorage } from "../auth/TokenStorage";

type Mode = "login" | "register";

const LoginScreen: React.FC = () => {
  const [mode, setMode] = useState<Mode>("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const navigate = useNavigate();

  const toggleMode = () => {
    setMode((prev) => (prev === "login" ? "register" : "login"));
    setError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const response =
        mode === "login"
          ? await authApi.login(username, password)
          : await authApi.register(username, password);
      TokenStorage.set(response.token);
      navigate("/");
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.message || err.message);
      } else {
        setError("An unknown error occurred");
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="p-4 max-w-sm w-full">
      <h1 className="text-2xl font-bold mb-4">
        {mode === "login" ? "Sign in" : "Create account"}
      </h1>
      {error.length > 0 && (
        <Card className="mb-4 p-3 text-sm text-danger">{error}</Card>
      )}
      <form onSubmit={handleSubmit}>
        <Input
          label="Username"
          placeholder="Enter your username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          autoComplete="username"
          required
          fullWidth
        />
        <Spacer y={1} />
        <Input
          label="Password"
          type="password"
          placeholder="Enter your password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          autoComplete={mode === "login" ? "current-password" : "new-password"}
          required
          fullWidth
        />
        <Spacer y={2} />
        <Button
          type="submit"
          color="primary"
          className="w-full"
          isDisabled={submitting}
        >
          {mode === "login" ? "Sign in" : "Register"}
        </Button>
      </form>
      <Spacer y={2} />
      <Button
        variant="light"
        className="w-full"
        onPress={toggleMode}
        isDisabled={submitting}
      >
        {mode === "login"
          ? "Need an account? Register"
          : "Already have an account? Sign in"}
      </Button>
    </div>
  );
};

export default LoginScreen;
