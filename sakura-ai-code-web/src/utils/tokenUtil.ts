
const TOKEN_NAME = 'Authorization'

function setToken(token: string) {
  localStorage.setItem(TOKEN_NAME, token)
}

function getToken() {
  return localStorage.getItem(TOKEN_NAME)
}
function removeToken() {
  localStorage.removeItem(TOKEN_NAME)
}
export {
  TOKEN_NAME,
  setToken,
  getToken,
  removeToken
}
