import "./LoginPage.css";

import logo from "../../assets/logo.svg";

import character250 from "../../assets/login/login-character-250.webp";
import character440 from "../../assets/login/login-character-440.webp";
import character500 from "../../assets/login/login-character-500.webp";
import character880 from "../../assets/login/login-character-880.webp";

function LoginPage() {
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
          <div className="login-form-wrapper">
            <h1>Welcome Back</h1>

            <form className="login-form">
              <div className="login-field">
                <label htmlFor="email">Email</label>

                <div className="login-input-wrapper">
                  <span aria-hidden="true">✉</span>

                  <input
                    id="email"
                    type="email"
                    placeholder="yourname@email.com"
                    autoComplete="email"
                  />
                </div>
              </div>

              <button type="submit">SEND CODE</button>

              <p>
                we'll send a verification code
                <br />
                to your email
              </p>
            </form>
          </div>
        </section>
      </div>
    </main>
  );
}

export default LoginPage;
