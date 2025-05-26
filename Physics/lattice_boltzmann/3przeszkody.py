import matplotlib.pyplot as plt
import numpy as np
from matplotlib import pyplot

plot_every = 50


def distance(x1, y1, x2, y2):
    return np.sqrt((x2 - x1) ** 2 + (y2 - y1) ** 2)


def main():
    """ Lattice Boltzmann Simulation """

    # Simulation parameters
    Nx = 400  # resolution x-dir
    Ny = 100  # resolution y-dir
    rho0 = 100  # average density
    tau = 0.53  # collision timescale
    Nt = 3000  # number of timesteps
    plotRealTime = True  # switch on for plotting as the simulation goes along

    # Lattice speeds / weights
    NL = 9
    cxs = np.array([0, 0, 1, 1, 1, 0, -1, -1, -1])
    cys = np.array([0, 1, 1, 0, -1, -1, -1, 0, 1])
    weights = np.array([4 / 9, 1 / 9, 1 / 36, 1 / 9, 1 / 36, 1 / 9, 1 / 36, 1 / 9, 1 / 36])  # sums to 1

    # Initial Conditions
    F = np.ones((Ny, Nx, NL)) + 0.01 * np.random.rand(Ny, Nx, NL)  # ones are more stable than zeros
    F += 0.01 * np.random.randn(Ny, Nx, NL)
    F[:, :, 3] = 2.3

    # Define obstacles
    cylinder = np.full((Ny, Nx), False)

    # Obstacle 1
    for y in range(0, Ny):
        for x in range(0, Nx):
            if distance(Nx // 4, Ny // 2, x, y) < 13:
                cylinder[y, x] = True

    # Obstacle 2
    for y in range(0, Ny):
        for x in range(0, Nx):
            if distance(Nx // 2, Ny // 3, x, y) < 13:
                cylinder[y, x] = True

    # Obstacle 3
    for y in range(0, Ny):
        for x in range(0, Nx):
            if distance(3 * Nx // 4, 2 * Ny // 3, x, y) < 13:
                cylinder[y, x] = True

    for it in range(Nt):
        print(it)

        for i, cx, cy in zip(range(NL), cxs, cys):
            F[:, :, i] = np.roll(F[:, :, i], cx, axis=1)
            F[:, :, i] = np.roll(F[:, :, i], cy, axis=0)

        bndryF = F[cylinder, :]
        bndryF = bndryF[:, [0, 5, 6, 7, 8, 1, 2, 3, 4]]

        # Fluid variables
        rho = np.sum(F, 2)
        ux = np.sum(F * cxs, 2) / rho
        uy = np.sum(F * cys, 2) / rho

        F[cylinder, :] = bndryF
        ux[cylinder] = 0
        uy[cylinder] = 0

        # Collision
        Feq = np.zeros(F.shape)
        for i, cx, cy, w in zip(range(NL), cxs, cys, weights):
            Feq[:, :, i] = rho * w * (
                    1 + 3 * (cx * ux + cy * uy) + 9 * (cx * ux + cy * uy) ** 2 / 2 - 3 * (ux ** 2 + uy ** 2) / 2
            )
        F = F + -(1 / tau) * (F - Feq)

        if it % plot_every == 0:
            pyplot.imshow(np.sqrt(ux ** 2 + uy ** 2), cmap='magma')
            pyplot.pause(.01)
            pyplot.cla()


if __name__ == "__main__":
    main()
