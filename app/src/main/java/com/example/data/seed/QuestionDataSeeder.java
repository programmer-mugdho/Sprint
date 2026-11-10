package com.example.data.seed;

import com.example.data.entity.QuestionEntity;

import java.util.ArrayList;
import java.util.List;

public class QuestionDataSeeder {

    public List<QuestionEntity> getInitialQuestions() {
        List<QuestionEntity> list = new ArrayList<>();

        // ==========================================
        // PHYSICS - Mechanics
        // ==========================================
        list.add(new QuestionEntity(
                "Physics", "Mechanics",
                "A projectile is fired at an angle of 30° to the horizontal with an initial speed of 40 m/s. What is its time of flight? (Take g = 9.8 m/s²)",
                "T = (2 * u * sin θ) / g",
                "2.04 s", "4.08 s", "6.12 s", "8.16 s",
                1,
                "T = (2 * 40 * sin 30°) / 9.8 = (80 * 0.5) / 9.8 = 40 / 9.8 ≈ 4.08 seconds.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Physics", "Mechanics",
                "A body of mass 5 kg moves in a circular path of radius 2 m with a constant speed of 4 m/s. The centripetal force acting on the body is:",
                "F_c = (m * v²) / r",
                "20 N", "40 N", "80 N", "10 N",
                1,
                "F = (5 * 4²) / 2 = (5 * 16) / 2 = 80 / 2 = 40 N.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Physics", "Mechanics",
                "A wheel of moment of inertia 2 kg·m² is rotating at 10 rad/s. A constant opposing torque of 4 N·m is applied to bring it to rest. The time taken to stop is:",
                "τ = I * α, ω = ω_0 - α * t",
                "2.5 s", "5.0 s", "7.5 s", "1.25 s",
                1,
                "Deceleration α = τ / I = 4 / 2 = 2 rad/s². Time to stop t = ω_0 / α = 10 / 2 = 5 seconds.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Physics", "Mechanics",
                "A spring of spring constant k = 200 N/m is compressed by 0.1 m. The potential energy stored in the spring is:",
                "U = 1/2 * k * x²",
                "2 J", "1 J", "0.5 J", "10 J",
                1,
                "U = 0.5 * 200 * (0.1)² = 100 * 0.01 = 1.0 Joule.",
                "Easy"
        ));

        // ==========================================
        // PHYSICS - Electricity & Magnetism
        // ==========================================
        list.add(new QuestionEntity(
                "Physics", "Electricity",
                "Two point charges of +3 µC and -3 µC are separated by 0.3 m in vacuum. What is the magnitude of electrostatic attraction between them? (k = 9 × 10⁹ N·m²/C²)",
                "F = k * |q1 * q2| / r²",
                "0.9 N", "9.0 N", "0.09 N", "90 N",
                0,
                "F = (9 × 10⁹ * 3 × 10⁻⁶ * 3 × 10⁻⁶) / (0.3)² = 0.081 / 0.09 = 0.9 N.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Physics", "Electricity",
                "Three identical capacitors, each of capacitance 6 µF, are connected in series. The equivalent capacitance is:",
                "1/C_eq = 1/C1 + 1/C2 + 1/C3",
                "18 µF", "2 µF", "6 µF", "3 µF",
                1,
                "In series: 1/C_eq = 1/6 + 1/6 + 1/6 = 3/6 = 1/2. Therefore C_eq = 2 µF.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Physics", "Electricity",
                "A current-carrying wire of length 0.5 m carrying 4 A is placed perpendicular to a magnetic field of 0.8 T. The magnetic force on the wire is:",
                "F = I * L * B * sin θ",
                "1.6 N", "3.2 N", "0.8 N", "2.0 N",
                0,
                "F = 4 * 0.5 * 0.8 * sin(90°) = 2 * 0.8 = 1.6 N.",
                "Medium"
        ));

        // ==========================================
        // PHYSICS - Waves & Thermodynamics
        // ==========================================
        list.add(new QuestionEntity(
                "Physics", "Thermodynamics",
                "A Carnot heat engine operates between a source at 500 K and a sink at 300 K. What is the theoretical maximum efficiency?",
                "η = 1 - (T_sink / T_source)",
                "30%", "40%", "60%", "50%",
                1,
                "Efficiency η = 1 - (300 / 500) = 1 - 0.60 = 0.40, which is 40%.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Physics", "Waves & Sound",
                "A sound source of frequency 500 Hz moves toward a stationary listener at 34 m/s. If the speed of sound is 340 m/s, the observed frequency is:",
                "f' = f * v / (v - v_s)",
                "450 Hz", "555.5 Hz", "600 Hz", "525 Hz",
                1,
                "f' = 500 * (340 / (340 - 34)) = 500 * (340 / 306) = 500 * 1.111 ≈ 555.5 Hz.",
                "Hard"
        ));

        // ==========================================
        // CHEMISTRY - Atomic Structure & Bonding
        // ==========================================
        list.add(new QuestionEntity(
                "Chemistry", "Atomic Structure",
                "Which set of quantum numbers (n, l, m_l, m_s) is NOT permissible for an electron in an atom?",
                "l in [0, n-1], m_l in [-l, +l], m_s = ±1/2",
                "n = 3, l = 2, m_l = -1, m_s = +1/2",
                "n = 2, l = 2, m_l = 0, m_s = -1/2",
                "n = 4, l = 0, m_l = 0, m_s = -1/2",
                "n = 1, l = 0, m_l = 0, m_s = +1/2",
                1,
                "Azimuthal quantum number 'l' must be strictly less than n (l can take values 0 up to n-1). When n=2, l cannot be 2.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Chemistry", "Atomic Structure",
                "The de Broglie wavelength of an electron moving with speed 2 × 10⁶ m/s is: (m_e = 9.1 × 10⁻³¹ kg, h = 6.626 × 10⁻³⁴ J·s)",
                "λ = h / (m * v)",
                "3.64 × 10⁻¹⁰ m", "7.28 × 10⁻¹⁰ m", "1.82 × 10⁻¹⁰ m", "5.46 × 10⁻¹⁰ m",
                0,
                "λ = (6.626 × 10⁻³⁴) / (9.1 × 10⁻³¹ * 2 × 10⁶) = 6.626 × 10⁻³⁴ / 1.82 × 10⁻²⁴ ≈ 3.64 × 10⁻¹⁰ m (0.364 nm).",
                "Hard"
        ));

        list.add(new QuestionEntity(
                "Chemistry", "Chemical Bonding",
                "What is the hybridization and molecular geometry of the central sulfur atom in Sulfur Hexafluoride (SF₆)?",
                "Steric Number = 6 bond pairs + 0 lone pairs = 6",
                "sp³d, Trigonal bipyramidal", "sp³d², Octahedral", "sp³d², Square planar", "sp³, Tetrahedral",
                1,
                "SF₆ has 6 bonding pairs and 0 lone pairs on sulfur, giving steric number 6, corresponding to sp³d² hybridization and octahedral geometry.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Chemistry", "Chemical Bonding",
                "Which of the following molecules has a permanent dipole moment (non-zero dipole moment)?",
                "Vector sum of bond dipoles",
                "CO₂", "BF₃", "NH₃", "CH₄",
                2,
                "NH₃ has a trigonal pyramidal shape with a lone pair on nitrogen, preventing cancellation of bond dipoles, giving a net dipole moment.",
                "Medium"
        ));

        // ==========================================
        // CHEMISTRY - Equilibrium & Organic
        // ==========================================
        list.add(new QuestionEntity(
                "Chemistry", "Equilibrium",
                "For the exothermic Haber synthesis: N₂(g) + 3H₂(g) ⇌ 2NH₃(g) (ΔH < 0), which condition favors maximum yield of Ammonia?",
                "Le Chatelier's Principle",
                "High temperature and low pressure",
                "Low temperature and high pressure",
                "High temperature and high pressure",
                "Low temperature and low pressure",
                1,
                "The forward reaction is exothermic (favored by lowering temperature) and reduces gas moles from 4 to 2 (favored by increasing pressure).",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Chemistry", "Organic Chemistry",
                "When Propene reacts with HBr in the absence of peroxides, the major organic product formed is:",
                "Markovnikov's Rule",
                "1-Bromopropane", "2-Bromopropane", "1,2-Dibromopropane", "Cyclopropane",
                1,
                "According to Markovnikov's rule, the electrophile H⁺ adds to the carbon with more hydrogens, creating the more stable secondary carbocation, yielding 2-bromopropane.",
                "Medium"
        ));

        // ==========================================
        // HIGHER MATH - Differential Calculus
        // ==========================================
        list.add(new QuestionEntity(
                "Higher Math", "Differential Calculus",
                "Evaluate the limit: lim (x -> 0) [sin(5x) / tan(3x)]",
                "lim (x -> 0) sin(ax)/x = a, lim (x -> 0) tan(bx)/x = b",
                "3/5", "5/3", "1", "15",
                1,
                "lim (x->0) [sin(5x)/5x * 5] / [tan(3x)/3x * 3] = (1 * 5) / (1 * 3) = 5/3.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Differential Calculus",
                "Find the slope of the tangent to the curve y = 3x² - 4x + 1 at the point where x = 2.",
                "dy/dx = 6x - 4",
                "6", "8", "10", "4",
                1,
                "dy/dx = 6x - 4. Substituting x = 2: dy/dx = 6(2) - 4 = 12 - 4 = 8.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Differential Calculus",
                "Find the maximum value of the function f(x) = 2x³ - 9x² + 12x + 5 on the interval [0, 3].",
                "f'(x) = 6x² - 18x + 12 = 0 => x = 1, 2",
                "9", "10", "14", "5",
                2,
                "f'(x) = 6(x - 1)(x - 2). Critical points: x = 1, 2. f(0) = 5, f(1) = 10, f(2) = 9, f(3) = 2(27) - 9(9) + 12(3) + 5 = 14. Max is 14.",
                "Hard"
        ));

        // ==========================================
        // HIGHER MATH - Integral Calculus & Coordinate Geometry
        // ==========================================
        list.add(new QuestionEntity(
                "Higher Math", "Integral Calculus",
                "Evaluate the definite integral: ∫ from 0 to π/2 [ (sin x) / (sin x + cos x) ] dx",
                "King's property: ∫_a^b f(x)dx = ∫_a^b f(a+b-x)dx",
                "π/2", "π/4", "π", "1",
                1,
                "Let I = ∫_0^(π/2) [sin x / (sin x + cos x)] dx. By symmetry I = ∫_0^(π/2) [cos x / (cos x + sin x)] dx. Adding both: 2I = ∫_0^(π/2) 1 dx = π/2 => I = π/4.",
                "Hard"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Integral Calculus",
                "Evaluate the integral: ∫ x * e^(2x) dx",
                "Integration by parts: ∫ u dv = u*v - ∫ v du",
                "(x/2 - 1/4) e^(2x) + C",
                "(x/2 + 1/4) e^(2x) + C",
                "(x - 1/2) e^(2x) + C",
                "x * e^(2x) + C",
                0,
                "Let u = x, dv = e^(2x)dx. du = dx, v = 1/2 * e^(2x). ∫ = x/2 * e^(2x) - ∫ 1/2 * e^(2x) dx = (x/2 - 1/4)e^(2x) + C.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Coordinate Geometry",
                "The perpendicular distance from the origin (0, 0) to the line 3x + 4y - 15 = 0 is:",
                "d = |Ax_0 + By_0 + C| / √(A² + B²)",
                "3 units", "5 units", "15 units", "2.5 units",
                0,
                "d = |3(0) + 4(0) - 15| / √(3² + 4²) = |-15| / 5 = 3 units.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Coordinate Geometry",
                "What is the radius of the circle given by x² + y² - 4x + 6y - 12 = 0?",
                "r = √(g² + f² - c)",
                "3", "4", "5", "6",
                2,
                "Here 2g = -4 => g = -2; 2f = 6 => f = 3; c = -12. Radius r = √((-2)² + 3² - (-12)) = √(4 + 9 + 12) = √25 = 5.",
                "Easy"
        ));

        // ==========================================
        // ENGLISH - Grammar & Prepositions
        // ==========================================
        list.add(new QuestionEntity(
                "English", "Grammar & Prepositions",
                "Choose the appropriate preposition: The research scientist was thoroughly absorbed _____ her investigation.",
                "Collocation: absorbed in",
                "with", "in", "at", "to",
                1,
                "The adjective 'absorbed' takes the preposition 'in' when denoting deep mental concentration.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "English", "Grammar & Prepositions",
                "Identify the grammatically correct sentence:",
                "Neither... nor takes verb agreeing with nearest subject",
                "Neither the professor nor the students was present in the lab.",
                "Neither the professor nor the students were present in the lab.",
                "Neither the students nor the professor were present in the lab.",
                "Neither the professor or the students were present in the lab.",
                1,
                "In 'neither... nor', the verb agrees in number with the closer subject. 'students' is plural, so 'were' is correct.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "English", "Grammar & Prepositions",
                "Fill in the blank: Had the engineer checked the structural blueprints, the catastrophic failure _____ avoided.",
                "Third Conditional: Had + S + V3, S + would have been + V3",
                "would be", "would have been", "will be", "had been",
                1,
                "This is an inverted third conditional (past counterfactual). The result clause requires 'would have been avoided'.",
                "Medium"
        ));

        // ==========================================
        // ENGLISH - Vocabulary & Analogies
        // ==========================================
        list.add(new QuestionEntity(
                "English", "Vocabulary",
                "What is the synonym of 'EPHEMERAL'?",
                "Ephemeral = lasting for a very short time",
                "Perpetual", "Transient", "Substantial", "Obscure",
                1,
                "'Ephemeral' means short-lived or fleeting; 'transient' is its exact synonym. 'Perpetual' is its antonym.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "English", "Vocabulary",
                "What is the antonym of the word 'PRAGMATIC'?",
                "Pragmatic = realistic, dealing with things sensibly",
                "Practical", "Idealistic", "Rational", "Methodical",
                1,
                "'Pragmatic' means practical and realistic. Its antonym is 'idealistic', which relates to visionary ideals rather than practical considerations.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "English", "Vocabulary",
                "Select the pair that expresses a relationship most similar to that in the given pair: ARCHITECT : BLUEPRINT ::",
                "Analogy: Creator to foundational design",
                "Musician : Instrument", "Novelist : Plot outline", "Carpenter : Saw", "Doctor : Hospital",
                1,
                "An architect prepares a blueprint before construction, just as a novelist drafts a plot outline before writing the narrative.",
                "Hard"
        ));

        // ==========================================
        // ADDITIONAL PHYSICS QUESTIONS
        // ==========================================
        list.add(new QuestionEntity(
                "Physics", "Waves & Optics",
                "In a Young's double slit experiment, if the distance between the slits is halved and distance to the screen is doubled, the fringe width becomes:",
                "β = (λ * D) / d",
                "Unchanged", "Doubled", "Four times", "Halved",
                2,
                "β_new = (λ * 2D) / (d / 2) = 4 * ((λ * D) / d) = 4 * β. The fringe width becomes four times.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Physics", "Modern Physics",
                "The half-life of a radioactive substance is 10 days. How much time will it take for 75% of the sample to decay?",
                "N = N0 * (1/2)^(t / T_half)",
                "15 days", "20 days", "30 days", "25 days",
                1,
                "When 75% decays, 25% (1/4) remains. (1/2)^2 = 1/4, so time t = 2 * half-life = 2 * 10 = 20 days.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Physics", "Modern Physics",
                "Work function of a metal is 2.5 eV. What is the threshold frequency for photoelectric emission? (h = 6.63 × 10⁻³⁴ J·s, 1 eV = 1.6 × 10⁻¹⁹ J)",
                "W_0 = h * f_0",
                "6.03 × 10¹⁴ Hz", "3.01 × 10¹⁴ Hz", "1.20 × 10¹⁵ Hz", "4.50 × 10¹³ Hz",
                0,
                "W_0 = 2.5 * 1.6 × 10⁻¹⁹ J = 4.0 × 10⁻¹⁹ J. f_0 = (4.0 × 10⁻¹⁹) / (6.63 × 10⁻³⁴) ≈ 6.03 × 10¹⁴ Hz.",
                "Hard"
        ));

        // ==========================================
        // ADDITIONAL CHEMISTRY QUESTIONS
        // ==========================================
        list.add(new QuestionEntity(
                "Chemistry", "Organic Chemistry",
                "Which compound reacts with Lucas Reagent (conc. HCl + anhydrous ZnCl2) immediately at room temperature?",
                "Lucas test speed: Tertiary > Secondary > Primary alcohol",
                "1-butanol", "2-butanol", "2-methyl-2-propanol", "ethanol",
                2,
                "2-methyl-2-propanol is a tertiary alcohol, so it forms a turbid solution immediately with Lucas reagent.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Chemistry", "Atomic Structure",
                "According to Bohr's model, the angular momentum of an electron in the 3rd orbit of hydrogen atom is:",
                "L = (n * h) / (2 * π)",
                "h / π", "1.5 h / π", "3 h / π", "2 h / π",
                1,
                "L = (3 * h) / (2 * π) = 1.5 h / π.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Chemistry", "Electrochemistry",
                "How many Faradays of electricity are required to deposit 1 mole of Aluminum from molten Al2O3?",
                "Al³⁺ + 3e⁻ -> Al",
                "1 F", "2 F", "3 F", "6 F",
                2,
                "Reduction of 1 mole of Al³⁺ requires 3 moles of electrons, which equals 3 Faradays of charge.",
                "Easy"
        ));

        // ==========================================
        // ADDITIONAL HIGHER MATH QUESTIONS
        // ==========================================
        list.add(new QuestionEntity(
                "Higher Math", "Integration",
                "Evaluate: ∫ (sin x) / (1 + cos² x) dx",
                "Substitution: u = cos x, du = -sin x dx",
                "-tan⁻¹(cos x) + C", "tan⁻¹(sin x) + C", "ln|1 + cos² x| + C", "-cot⁻¹(sin x) + C",
                0,
                "Let u = cos x, then du = -sin x dx. ∫ -du / (1 + u²) = -tan⁻¹(u) + C = -tan⁻¹(cos x) + C.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Vectors",
                "If vector A = 2i + 3j - k and vector B = i - j + 2k, what is the dot product A · B?",
                "A · B = AxBx + AyBy + AzBz",
                "-3", "-1", "3", "5",
                1,
                "A · B = (2 * 1) + (3 * -1) + (-1 * 2) = 2 - 3 - 2 = -3.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Trigonometry",
                "What is the maximum value of 3 sin x + 4 cos x?",
                "Max value of a sin x + b cos x is √(a² + b²)",
                "5", "7", "12", "1",
                0,
                "Max value = √(3² + 4²) = √(9 + 16) = √25 = 5.",
                "Easy"
        ));

        list.add(new QuestionEntity(
                "Higher Math", "Matrices & Determinants",
                "If matrix A is singular, what is the value of x in matrix | 2  x | / | 4  6 |?",
                "Singular matrix => |A| = 0",
                "2", "3", "4", "6",
                1,
                "Det = (2 * 6) - (x * 4) = 12 - 4x = 0 => 4x = 12 => x = 3.",
                "Easy"
        ));

        // ==========================================
        // ADDITIONAL ENGLISH QUESTIONS
        // ==========================================
        list.add(new QuestionEntity(
                "English", "Voice & Narration",
                "Change into Passive Voice: 'Who wrote the university entry guide?'",
                "Interrogative passive: By whom + was/were + object + V3",
                "By whom was the university entry guide written?",
                "By whom the university entry guide was written?",
                "Who was written the university entry guide by?",
                "By whom has the university entry guide been written?",
                0,
                "'Who' becomes 'By whom', past tense takes 'was + V3', maintaining question structure: 'By whom was the university entry guide written?'.",
                "Medium"
        ));

        list.add(new QuestionEntity(
                "English", "Grammar & Prepositions",
                "Choose the appropriate idiom meaning 'to deal with a difficult situation directly and bravely':",
                "Idiomatic expression",
                "Take the bull by the horns", "Burn the candle at both ends", "Cry over spilt milk", "Bark up the wrong tree",
                0,
                "'To take the bull by the horns' means to deal decisively with a difficult situation.",
                "Easy"
        ));

        return list;
    }
}
