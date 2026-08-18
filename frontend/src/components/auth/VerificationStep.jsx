import { useEffect, useRef, useState } from "react";

function VerificationStep({
  email,
  onChangeEmail,
  onVerify,
  isLoading,
  error,
}) {
  const [code, setCode] = useState(Array(6).fill(""));

  const inputsRef = useRef([]);

  const [resendSeconds, setResendSeconds] = useState(42);

  const [emailCopied, setEmailCopied] = useState(false);

  useEffect(() => {
    if (resendSeconds <= 0) {
      return;
    }

    const interval = setInterval(() => {
      setResendSeconds((currentSeconds) => currentSeconds - 1);
    }, 1000);

    return () => {
      clearInterval(interval);
    };
  }, [resendSeconds]);

  function handleChange(index, value) {
    const digit = value.replace(/\D/g, "");

    if (!digit) {
      const newCode = [...code];
      newCode[index] = "";
      setCode(newCode);
      return;
    }

    const newCode = [...code];

    newCode[index] = digit.slice(-1);

    setCode(newCode);

    if (index < 5) {
      inputsRef.current[index + 1]?.focus();
    }
  }

  function handleKeyDown(index, event) {
    if (event.key === "Backspace" && !code[index] && index > 0) {
      inputsRef.current[index - 1]?.focus();
    }
  }

  function handlePaste(event) {
    event.preventDefault();

    const pastedCode = event.clipboardData
      .getData("text")
      .replace(/\D/g, "")
      .slice(0, 6);

    if (!pastedCode) {
      return;
    }

    const newCode = Array(6).fill("");

    pastedCode.split("").forEach((digit, index) => {
      newCode[index] = digit;
    });

    setCode(newCode);

    const lastIndex = Math.min(pastedCode.length, 6) - 1;

    inputsRef.current[lastIndex]?.focus();
  }

  async function handleCopyEmail() {
    try {
      await navigator.clipboard.writeText(email);

      setEmailCopied(true);

      setTimeout(() => {
        setEmailCopied(false);
      }, 1500);
    } catch {
      setEmailCopied(false);
    }
  }

  function handleResendCode() {
    if (resendSeconds > 0) {
      return;
    }
    setResendSeconds(60);
  }

  function handleSubmit(event) {
    event.preventDefault();

    const verificationCode = code.join("");

    onVerify(verificationCode);
  }

  return (
    <div className="verification-wrapper">
      <header className="verification-header">
        <h1>Verify Code</h1>

        <p>
          Enter the 6-digit code sent to your email
          <br />
          to continue
        </p>
      </header>

      <div className="verification-email">
        <span>Code sent to </span>

        <button
          type="button"
          className="verification-email-link"
          onClick={handleCopyEmail}
          title="Copy email"
        >
          {email || "joao@email.com"}
        </button>

        {emailCopied && (
          <span className="verification-email-copied">Copied!</span>
        )}
      </div>

      <form className="verification-form" onSubmit={handleSubmit}>
        <div className="verification-field">
          <label>Verification Code</label>

          <div className="verification-inputs" onPaste={handlePaste}>
            {code.map((digit, index) => (
              <input
                key={index}
                ref={(element) => {
                  inputsRef.current[index] = element;
                }}
                type="text"
                inputMode="numeric"
                autoComplete={index === 0 ? "one-time-code" : "off"}
                maxLength="1"
                value={digit}
                aria-label={`Digit ${index + 1}`}
                onChange={(event) => handleChange(index, event.target.value)}
                onKeyDown={(event) => handleKeyDown(index, event)}
              />
            ))}
          </div>
        </div>

        <button
          className="verification-button"
          type="submit"
          disabled={isLoading}
        >
          {isLoading ? "VERIFYING..." : "VERIFY CODE"}
        </button>
        {error && (
          <p className="verification-error" role="alert">
            {error}
          </p>
        )}
      </form>

      <button
        type="button"
        className="verification-change-email"
        onClick={onChangeEmail}
      >
        Change email
      </button>

      <div className="verification-resend">
        {resendSeconds > 0 ? (
          <span>Resend code in {resendSeconds}s</span>
        ) : (
          <button type="button" onClick={handleResendCode}>
            Resend code
          </button>
        )}
      </div>
    </div>
  );
}

export default VerificationStep;
