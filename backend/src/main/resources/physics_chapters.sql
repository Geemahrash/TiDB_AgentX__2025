-- Table: physics_chapters 
CREATE TABLE IF NOT EXISTS physics_chapters ( 
  chapter_no INTEGER PRIMARY KEY, 
  title TEXT NOT NULL, 
  summary TEXT NOT NULL 
); 

INSERT INTO physics_chapters (chapter_no, title, summary) VALUES 
(1, 'General Physics (Units, Dimensions & Measurement)', 'Foundations of measurement: fundamental & derived quantities, SI units, dimensional analysis, measurement errors, significant figures, and common measuring tools.'), 
(2, 'Kinematics', 'Description of motion: displacement, velocity, acceleration; equations of motion, projectile motion, relative velocity, graphs, and uniform circular motion.'), 
(3, 'Laws of Motion', 'Newton''s three laws, inertia, F=ma, action-reaction, free-body diagrams, friction, and applications to collisions and inclined planes.'), 
(4, 'Work, Energy & Power', 'Work as force·displacement, kinetic & potential energy, work–energy theorem, conservation of energy, power, collisions, and efficiency.'), 
(5, 'System of Particles & Rotational Motion', 'Center of mass, linear momentum and impulse for systems, rotational kinematics and dynamics, torque, moment of inertia, and rolling motion.'), 
(6, 'Gravitation', 'Newton''s law of gravitation, gravitational field & potential, variation of g, orbital motion, escape velocity, and Kepler''s laws.'), 
(7, 'Oscillations', 'Simple harmonic motion, mass–spring and pendulum, amplitude/period/frequency, energy exchange, damping, forced oscillations, and resonance.'), 
(8, 'Waves', 'Mechanical vs electromagnetic waves, wavelength/frequency/velocity, superposition, interference, beats, stationary waves, and Doppler effect.'), 
(9, 'Properties of Solids and Liquids', 'Elasticity and Hooke''s law, moduli, pressure and buoyancy, viscosity, surface tension, Bernoulli''s theorem and applications.'), 
(10, 'Heat and Thermodynamics', 'Temperature scales, heat transfer (conduction/convection/radiation), specific heat, thermal expansion, and the laws of thermodynamics (Zeroth to Third).'), 
(11, 'Kinetic Theory of Gases', 'Microscopic model of gases, RMS/average/most-probable speeds, ideal gas law from molecular view, degrees of freedom, and real-gas corrections.'), 
(12, 'Electrostatics', 'Charges at rest, Coulomb''s law, electric field and potential, Gauss''s law, conductors/insulators, capacitors and dielectrics.'), 
(13, 'Current Electricity', 'Electric current, Ohm''s law, resistivity, circuits (series/parallel), Kirchhoff''s laws, EMF and internal resistance, and measurement bridges.'), 
(14, 'Magnetic Effects of Current', 'Magnetic fields from currents, Biot–Savart & Ampere''s laws, Lorentz force, force on conductors, and applications like motors and cyclotrons.'), 
(15, 'Magnetism and Matter', 'Magnetic dipole moment, Earth''s magnetism, diamagnetic/paramagnetic/ferromagnetic materials, hysteresis, and magnetic properties of materials.'), 
(16, 'Electromagnetic Induction', 'Faraday''s law and Lenz''s law, induced EMF, self and mutual induction, eddy currents, and applications in generators and transformers.'), 
(17, 'Alternating Current', 'Sinusoidal voltages/currents, RMS values, RLC circuits, resonance, phasors, power factor, and AC generation/transformers.'), 
(18, 'Electromagnetic Waves', 'Maxwell''s unification, propagation at speed of light, EM spectrum overview, transverse nature, and applications across frequencies.'), 
(19, 'Ray Optics and Optical Instruments', 'Reflection/refraction, Snell''s law, mirrors and lenses, ray diagrams, total internal reflection, and optical instruments.'), 
(20, 'Wave Optics', 'Huygens'' principle, interference, diffraction, Young''s double-slit, polarization, and wave phenomena beyond ray optics.'), 
(21, 'Dual Nature of Matter and Radiation', 'Wave–particle duality: photoelectric effect (photons), E=hf, de Broglie wavelength for matter, and experimental confirmations.'), 
(22, 'Atoms', 'Historical atomic models (Thomson, Rutherford, Bohr), quantized energy levels, spectral lines, and move toward quantum descriptions.'), 
(23, 'Nuclei', 'Nuclear composition, binding energy, mass defect, radioactivity (alpha/beta/gamma), fission and fusion, and nuclear applications.'), 
(24, 'Electronic Devices (Semiconductors)', 'Intrinsic/extrinsic semiconductors, p–n junctions, diodes, transistors, LEDs, solar cells, and basics of semiconductor device operation.'), 
(25, 'Communication Systems', 'Transmitter–channel–receiver model, analog/digital modulation (AM/FM/pulse), bandwidth, SNR, propagation modes, and telecom applications.');