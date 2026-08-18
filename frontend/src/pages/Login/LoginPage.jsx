import { useState } from "react";

import "./LoginPage.css";

import logo from "../../assets/logo.svg";

import character250 from "../../assets/login/login-character-250.webp";
import character440 from "../../assets/login/login-character-440.webp";
import character500 from "../../assets/login/login-character-500.webp";
import character880 from "../../assets/login/login-character-880.webp";

import EmailStep from "../../components/auth/EmailStep";
import VerificationStep from "../../components/auth/VerificationStep";

function LoginPage() {
  const [step, setStep] = useState("email");
  const [email, setEmail] = useState("");

  function handleEmailSubmit() {
    setStep("verification");
  }

  function handleChangeEmail() {
    setStep("email");
  }

  return (
    <main className="login-page">
      <div className="login-layout container">
        <section className="login-visual">
          <img className="login-logo" src={logo} alt="Guimarães" />

          <div className="login-character-background">
            <img
              className="login-character"
              src={character440}
              srcSet={`
                ${character250} 250w,
                ${character440} 440w,
                ${character500} 500w,
                ${character880} 880w
              `}
              sizes="(max-width: 600px) 250px, 440px"
              width="440"
              height="440"
              alt=""
              fetchPriority="high"
              decoding="async"
            />
          </div>
        </section>

        <section className="login-content">
          {step === "email" && (
            <EmailStep
              email={email}
              setEmail={setEmail}
              onSubmit={handleEmailSubmit}
            />
          )}

          {step === "verification" && (
            <VerificationStep email={email} onChangeEmail={handleChangeEmail} />
          )}
        </section>
      </div>
    </main>
  );
}

export default LoginPage;
