function EmailStep({ email, setEmail, onSubmit }) {
  function handleSubmit(event) {
    event.preventDefault();

    onSubmit();
  }

  return (
    <div className="login-form-wrapper">
      <h1>Welcome Back</h1>

      <form className="login-form" onSubmit={handleSubmit}>
        <div className="login-field">
          <label htmlFor="email">Email</label>

          <div className="login-input-wrapper">
            <span aria-hidden="true">✉</span>

            <input
              id="email"
              type="email"
              placeholder="yourname@email.com"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
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
  );
}

export default EmailStep;
