import { useState } from "react";

import "./LoginPage.css";

import logo from "../../assets/logo.svg";

import character250 from "../../assets/login/login-character-250.webp";
import character440 from "../../assets/login/login-character-440.webp";
import character500 from "../../assets/login/login-character-500.webp";
import character880 from "../../assets/login/login-character-880.webp";

import EmailStep from "../../components/auth/EmailStep";
import VerificationStep from "../../components/auth/VerificationStep";

import { requestVerificationCode } from "../../services/authService";

function LoginPage() {
  const [step, setStep] = useState("email");

  const [email, setEmail] = useState("");

  const [isRequestingCode, setIsRequestingCode] = useState(false);

  const [requestError, setRequestError] = useState("");

  function handleEmailChange(value) {
    setEmail(value);

    if (requestError) {
      setRequestError("");
    }
  }

  async function handleEmailSubmit() {
    const normalizedEmail = email.trim();

    if (!normalizedEmail) {
      setRequestError("Please enter your email address.");
      return;
    }

    if (isRequestingCode) {
      return;
    }

    setIsRequestingCode(true);
    setRequestError("");

    try {
      await requestVerificationCode(normalizedEmail);

      setEmail(normalizedEmail);

      setStep("verification");
    } catch (error) {
      if (error.status === 429) {
        setRequestError("Please wait before requesting another code.");

        return;
      }

      if (error.status === 400) {
        setRequestError("Please enter a valid email address.");

        return;
      }

      setRequestError(
        "We couldn't send the verification code. Please try again.",
      );
    } finally {
      setIsRequestingCode(false);
    }
  }

  function handleChangeEmail() {
    setStep("email");
    setRequestError("");
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
              onEmailChange={handleEmailChange}
              onSubmit={handleEmailSubmit}
              isLoading={isRequestingCode}
              error={requestError}
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
