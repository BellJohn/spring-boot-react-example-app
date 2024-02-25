import { render, screen } from '@testing-library/react';
import App from './App';

test('renders learn react link', () => {
  render(<App />);

  const homeLink = screen.getByText("Home");
  expect(homeLink).toBeInTheDocument();

  const clientsLink = screen.getByText("Clients");
  expect(clientsLink).toBeInTheDocument();
});
